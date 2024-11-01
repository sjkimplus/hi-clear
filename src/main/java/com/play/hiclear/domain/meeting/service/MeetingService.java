package com.play.hiclear.domain.meeting.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateEditRequest;
import com.play.hiclear.domain.meeting.dto.response.*;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.meeting.enums.SortType;
import com.play.hiclear.domain.meeting.repository.MeetingQueryDslRepository;
import com.play.hiclear.domain.meeting.repository.MeetingRepository;
import com.play.hiclear.domain.participant.dto.ParticipantResponse;
import com.play.hiclear.domain.participant.entity.Participant;
import com.play.hiclear.domain.participant.enums.ParticipantRole;
import com.play.hiclear.domain.participant.repository.ParticipantRepository;
import com.play.hiclear.domain.participant.service.ParticipantService;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) // 데이터를
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;
    private final MeetingQueryDslRepository meetingQueryDslRepository;

    /**
     * 번개 생성
     * @param authUser
     * @param request
     * @return
     */
    @Transactional
    public String create(AuthUser authUser, MeetingCreateEditRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        Meeting meeting = new Meeting(request, user);
        meetingRepository.save(meeting);

        // participant 에 추가
        participantService.add(authUser, meeting.getId());
        return ("번개 생성 성공"); // 이넘으로
    }

    /**
     * 번개 수정
     * @param authUser
     * @param request
     * @param meetingId
     * @return
     */
    @Transactional
    public String update(AuthUser authUser, MeetingCreateEditRequest request, Long meetingId) {
        // 업로드한 번개 일정 찾기
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        // 권한체크 - 작성자가 맞는지 체크
        checkAuthority(authUser, meeting);
        
        meeting.update(request);
        return "번개 수정 성공";
    }

    /**
     * 번개 삭제
     * @param authUser
     * @param meetingId
     * @return
     */
    @Transactional
    public String delete(AuthUser authUser, Long meetingId) {
        // 서비스내에서 private method으로 빼기
        // 업로드한 번개 일정 찾기
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        // 권한체크 - 작성자가 맞는지 체크
        checkAuthority(authUser, meeting);

        meeting.markDeleted();
        return "번개 삭제 성공";
    }

    /**
     * 번개글 단건 조회 + 신청한 번개 단건 조회 (특별 권한 없는 일반 조회)
     * @param meetingId
     * @return
     */
    public MeetingDetailResponse get(Long meetingId) {
        // 번개 일정 찾기
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        // 참여 완료한 사람수 구하기
        int numberJoined = participantService.getJoinedNumber(meeting.getId());
        return new MeetingDetailResponse(meeting, numberJoined);
    }

    /**
     * 개최한 번개 단건 조회
     * @param authUser
     * @param meetingId
     * @return
     */
    public MyMeetingDetailResponse getMyMeeting(AuthUser authUser, Long meetingId) {
        // 번개 일정 찾기
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        // 권한체크
        checkAuthority(authUser, meeting);

        // 번개 신청자들 중 APPROVE 된 신청자들 수
        int numberJoined = participantService.getJoinedNumber(meeting.getId());

        // 번개 신청자들 중 PENDING 상태인 것들 모으기
        List<ParticipantResponse> pendingParticipants = participantService.getPendingParticipants(meeting);

        return new MyMeetingDetailResponse(meeting, numberJoined, pendingParticipants);
    }

    /**
     * 나의 번개 (신청/개최) 다건 조회
     * @param authUser
     * @param role
     * @return
     */
    // earlierst 로 정렬 추가
    public MyMeetingResponses searchMyMeetings(AuthUser authUser, ParticipantRole role, Boolean includePassed) {

        List<Participant> participantList = participantRepository.findByUserIdAndRoleWithConditionalEndTime(authUser.getUserId(), role, includePassed);
        List<MyMeetingResponse> responseList;

        // if - return, early return 방식적용
        if (role==ParticipantRole.HOST) { // role = HOST인 경우
            // meetingList를 DTO로 변환
            responseList = participantList
                    .stream()
                    .map(p -> new MyMeetingResponse(p.getMeeting()))
                    .collect(Collectors.toList());
        } else {
            // meetingList를 DTO로 변환
            responseList = participantList
                    .stream()
                    .map(p -> new MyMeetingResponse(p.getMeeting(), p.getStatus()))
                    .collect(Collectors.toList());
        }
        return new MyMeetingResponses(responseList);
    }

    /**
     * 번개 통합검색 (급수필터, 정렬타입 적용)
     * @param sortType
     * @param rank
     * @param page
     * @param size
     * @return
     */
    public Page<MeetingSearchResponse> search(SortType sortType, Ranks rank, int page, int size) {
        Pageable pageable = PageRequest.of(page -1, size);
        return meetingQueryDslRepository.search(sortType, rank, pageable);
    }

    /**
     * 완료된 미팅은 완료 표기
     * @param meetingId
     * @return
     */
    public String finishMyMeeting(AuthUser authUser, Long meetingId) { // auth 인증
        // 번개 일정 찾기
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        // 권한체크
        checkAuthority(authUser, meeting);

        // 현재시간이 미팅종료 시간 이후인지 확인


        meeting.markFinished();
        return "번개 완료 성공";
    }

    private void checkAuthority(AuthUser authUser, Meeting meeting) {
        if (authUser.getUserId()!=meeting.getUser().getId()){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }
    }
}