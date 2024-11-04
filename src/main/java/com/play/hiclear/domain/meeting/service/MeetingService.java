package com.play.hiclear.domain.meeting.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.message.SuccessMessage;
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

import java.time.LocalDateTime;
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
        // 시간 체크 - 최소 한시간, 시작시간은 현재 이후만 가능
        checkTimeValidity(request);
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        Meeting meeting = new Meeting(request, user);
        meetingRepository.save(meeting);

        // participant 에 추가
        participantService.add(authUser, meeting.getId());
        return SuccessMessage.customMessage(SuccessMessage.CREATED, Meeting.class.getSimpleName());
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

        // 시간 체크 - 최소 한시간, 시작시간은 현재 이후만 가능
        checkTimeValidity(request);

        // 권한체크 - 작성자가 맞는지 체크
        checkAuthority(authUser, meeting);
        
        meeting.update(request);
        return SuccessMessage.customMessage(SuccessMessage.MODIFIED, Meeting.class.getSimpleName());
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
        return SuccessMessage.customMessage(SuccessMessage.DELETED, Meeting.class.getSimpleName());
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
        int numberJoined = participantService.getJoinedNumber(meeting);
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
        int numberJoined = participantService.getJoinedNumber(meeting);

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
    public MyMeetingResponses searchMyMeetings(AuthUser authUser, ParticipantRole role) {
        // 삭제되지 않고 완료처리 되지 않은 미틴에서의 participant 정보만 반환 (빠른 날짜 순서로 정렬)
        List<Participant> participantList = participantRepository.findByUserIdAndRoleExcludingFinished(authUser.getUserId(), role);
        List<MyMeetingResponse> responseList;

        // role = HOST인 경우
        if (role==ParticipantRole.HOST) {
            // meetingList를 DTO로 변환
            responseList = participantList
                    .stream()
                    .map(p -> new MyMeetingResponse(p.getMeeting()))
                    .collect(Collectors.toList());
            return new MyMeetingResponses(responseList);
        }
        // role = GUEST 인 경우
        responseList = participantList
                .stream()
                .map(p -> new MyMeetingResponse(p.getMeeting(), p.getStatus()))
                .collect(Collectors.toList());
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
        if (meeting.getEndTime().isAfter(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.TOO_SOON);
        }

        meeting.markFinished();
        return SuccessMessage.customMessage(SuccessMessage.MEETING_FINISHED);
    }

    private void checkAuthority(AuthUser authUser, Meeting meeting) {
        if (authUser.getUserId()!=meeting.getUser().getId()){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }
    }

    private void checkTimeValidity(MeetingCreateEditRequest request) {
        if (!request.getStartTime().isAfter(LocalDateTime.now()) || request.getEndTime().isBefore((request.getStartTime().plusHours(1)))) {
            throw new CustomException(ErrorCode.INVALID_TIME);
        }
    }
}