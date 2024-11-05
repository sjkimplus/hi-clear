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
import com.play.hiclear.domain.review.entity.Review;
import com.play.hiclear.domain.review.enums.MannerRank;
import com.play.hiclear.domain.review.repository.ReviewRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private ParticipantRepository participantRepository;
    private UserRepository userRepository;
    private ReviewRepository reviewRepository;
    private MeetingRepository meetingRepository;

    // 점수 계산
    private void updateUserStatistics(User reviewee){
        List<Review> reviews = reviewRepository.findByReviewee(reviewee);

        if(!reviews.isEmpty()){
            double averageMannerScore = reviews.stream()
                    .mapToInt(review -> getMannerScore(review.getMannerRank()))
                    .average()
                    .orElse(0);

            double averageGradeRank = reviews.stream()
                    .mapToInt(review -> getGradeRank(review.getGradeRank()))
                    .average()
                    .orElse(0);

            System.out.println("Average Manner Score for user " + reviewee.getId() + ": " + averageMannerScore);
            System.out.println("Average Grade Rank for user " + reviewee.getId() + ": " + averageGradeRank);
        }
    }

    private int getMannerScore(MannerRank mannerRank){
        return mannerRank.getMannerScore();
    }

    private int getGradeRank(Ranks ranks){
        return ranks.getRankValue();
    }

    /**
     *
     * @param authUser
     * @return
     */
    public List<User> search(AuthUser authUser) {

        Long reviewerId = authUser.getUserId();

        // 이미 종료된 미팅 필터
        List<Meeting> finishedMeetings = participantRepository.findFinishedMeetings();

        // 종료된 미팅 참가자 목록을 저장할 리스트
        List<User> reviewableUsers = new ArrayList<>();

        for(Meeting meeting : finishedMeetings){
            List<Participant> participants = participantRepository.findByMeeting(meeting);
            reviewableUsers.addAll(participants.stream()
                    .map(Participant::getUser)
                    .filter(user -> !user.getId().equals(reviewerId)) //로그인된 사용자는 제외
                    .distinct()
                    .toList()
            );
        }
        return reviewableUsers;
    }

    /**
     *
     * @param meetingId
     * @param request
     * @param authUser
     * @return ReviewCreateResponse
     */
    @Transactional
    public ReviewCreateResponse create(Long meetingId, ReviewCreateRequest request, AuthUser authUser) {
        Long reviewerId = authUser.getUserId();

        // 리뷰 하는사람 조회
        User reviewer = findUserById(reviewerId);

        // 리뷰 받는사람 조회
        User reviewee = findUserById(request.getRevieweeId());

        // 미팅 조회
        Meeting meeting = findMeetingById(meetingId);

        //미팅안에 리뷰 받는사람이 속해있는지 검증
        checkParticipant(meeting, reviewee);

        Review review = new Review(
                reviewer,
                reviewee,
                meeting,
                request.getMannerRank(),
                request.getGradeRank()
        );
        reviewRepository.save(review);

        updateUserStatistics(reviewee);

        return new ReviewCreateResponse(
                review.getId(),
                reviewer.getId(),
                reviewee.getId(),
                review.getMannerRank().name(),
                review.getGradeRank().name()
        );
    }

    // 유저 조회
    private User findUserById(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);
    }

    // 모임 조회
    private Meeting findMeetingById(Long meetingId) {
        return meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);
    }

    // 리뷰 받는사람이 모임에 속해있는지 확인
    private void checkParticipant(Meeting meeting, User reviewee) {
        Optional<Participant> participant = participantRepository.findByMeetingAndUser(meeting, reviewee);
        if(!participant.isPresent()){
            throw new CustomException(ErrorCode.REVIEW_MEETING_USER);
            // 해당 유저가 미팅에 속해있지않습니다.
        }
    }

}
