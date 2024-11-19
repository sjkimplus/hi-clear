package com.play.hiclear.domain.review.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.meeting.repository.MeetingRepository;
import com.play.hiclear.domain.participant.entity.Participant;
import com.play.hiclear.domain.participant.repository.ParticipantRepository;
import com.play.hiclear.domain.review.dto.request.ReviewCreateRequest;
import com.play.hiclear.domain.review.dto.response.ReviewCreateResponse;
import com.play.hiclear.domain.review.dto.response.ReviewSearchResponse;
import com.play.hiclear.domain.review.dto.response.UserStatisticsResponse;
import com.play.hiclear.domain.review.entity.Review;
import com.play.hiclear.domain.review.enums.MannerRank;
import com.play.hiclear.domain.review.repository.ReviewRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final MeetingRepository meetingRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 리뷰 가능한 유저 리스트로 불러오기
     *
     * @param authUser
     * @return
     */
    public List<ReviewSearchResponse> search(AuthUser authUser) {

        Long reviewerId = authUser.getUserId();

        // 이미 종료된 미팅 필터
        List<Participant> finishedParticipants = participantRepository.findFinishedMeetingsUserJoined(reviewerId); // 미팅이 끝난 것만 찾는다

        // 종료된 미팅 참가자 목록을 저장할 리스트
        List<ReviewSearchResponse> reviewableUsers = new ArrayList<>();

        for (Participant participant : finishedParticipants) {
            User user = participant.getUser();
            Meeting meeting = participant.getMeeting();
            if (!user.getId().equals(reviewerId)) {
                // 이미 리뷰를 작성한 건은 필터링
                boolean reviewed = reviewRepository.existsByMeetingAndRevieweeAndReviewer(meeting, user, userRepository.findByIdAndDeletedAtIsNullOrThrow(reviewerId));
                if (!reviewed) {
                    ReviewSearchResponse response = new ReviewSearchResponse(
                            user.getName(),
                            meeting.getTitle(),
                            meeting.getRegionAddress(),
                            meeting.getEndTime()
                    );
                    reviewableUsers.add(response);
                }
            }
        }
        return reviewableUsers;
    }

    /**
     * 리뷰 생성
     *
     * @param meetingId
     * @param request
     * @param authUser
     * @return ReviewCreateResponse
     */
    @Transactional
    public ReviewCreateResponse create(Long meetingId, ReviewCreateRequest request, AuthUser authUser) {
        Long reviewerId = authUser.getUserId();
//        Long revieweeId = request.getRevieweeId();

        // 리뷰 하는사람, 받는사람, 미팅 조회
        User reviewer = userRepository.findByIdAndDeletedAtIsNullOrThrow(reviewerId);
        User reviewee = userRepository.findByIdAndDeletedAtIsNullOrThrow(request.getRevieweeId());
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        // 리뷰 중복 여부 확인
        boolean reviewed = reviewRepository.existsByMeetingAndRevieweeAndReviewer(meeting, reviewee, reviewer);
        if (reviewed) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // meetingId와 reviewerId를 기반으로 락 키 생성
        String lockKey = "review_lock_" + meetingId + ":" + reviewerId;

        // 락 획득 시도

        boolean lockAcquired = tryLock(lockKey, 10, TimeUnit.SECONDS); // 10초 동안 락 시도
        try{//
            Thread.sleep(100);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        if(!lockAcquired){
            throw new CustomException(ErrorCode.LOCK_ACQUISITION_FAILED);
        }

        try{
            // 미팅안에 리뷰 받는사람이 속해있는지 검증
            checkParticipant(meeting, reviewee);

            // 리뷰 생성
            Review review = new Review(
                    reviewer,
                    reviewee,
                    meeting,
                    request.getMannerRank(),
                    request.getGradeRank()
            );
            reviewRepository.save(review);

            // 리뷰 반환
            return new ReviewCreateResponse(
                    review.getId(),
                    reviewer.getId(),
                    reviewee.getId(),
                    review.getMannerRank().name(),
                    review.getGradeRank().name()
            );
        } finally {
            // 락 해제
            unlock(lockKey);
        }

    }

    //유저의 아이디를 받아 해당 유저의 점수들을 반환
    public UserStatisticsResponse statistics(Long userId) {

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);

        MannerRank mannerRank = updateUserMannerScore(user);
        Ranks gradeRank = updateUserGradeRank(user);

        return new UserStatisticsResponse(mannerRank.name(), gradeRank.name());

    }

    // 매너 점수 평균을 계산후 소수점을 버리고 해당 값에 따른 enum을 반환
    public MannerRank updateUserMannerScore(User reviewee){
        List<Review> reviews = reviewRepository.findByReviewee(reviewee);
        if(reviews.isEmpty()){
            return MannerRank.LOW;
        }

        double averageMannerScore = reviews.stream()
                .mapToInt(review -> getMannerScore(review.getMannerRank()))
                .average()
                .orElse(0);

        int roundedScore = (int) Math.floor(averageMannerScore);
        return MannerRank.fromScore(roundedScore);
    }

    // 평가 급수 평균을 계산후 소수점을 버리고 해당 값에 따른 enum을 반환
    public Ranks updateUserGradeRank(User reviewee){
        List<Review> reviews = reviewRepository.findByReviewee(reviewee);
        if(reviews.isEmpty()){
            return Ranks.RANK_F;
        }
        double averageGradeRank = reviews.stream()
                .mapToInt(review -> getGradeRank(review.getGradeRank()))
                .average()
                .orElse(0);

        int roundedRank = (int) Math.floor(averageGradeRank);
        return Ranks.fromRankValue(roundedRank);
    }

    private int getMannerScore(MannerRank mannerRank) {
        return mannerRank.getMannerScore();
    }

    private int getGradeRank(Ranks ranks) {
        return ranks.getRankValue();
    }

    // 리뷰 받는사람이 모임에 속해있는지 확인
    private void checkParticipant(Meeting meeting, User reviewee) {
        Optional<Participant> participant = participantRepository.findByMeetingAndUser(meeting, reviewee);
        if (!participant.isPresent()) {
            throw new CustomException(ErrorCode.REVIEW_MEETING_USER);
            // 해당 유저가 미팅에 속해있지않습니다.
        }
    }

    private boolean tryLock(String lockKey, long timeout, TimeUnit timeUnit) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", timeout, timeUnit);
        return success != null && success;
    }

    private void unlock(String lockKey) {
        redisTemplate.delete(lockKey);
    }

}
