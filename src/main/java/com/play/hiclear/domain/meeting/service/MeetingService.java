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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;
    private final MeetingQueryDslRepository meetingQueryDslRepository;

    @Transactional
    public String create(AuthUser authUser, MeetingCreateEditRequest request) {
        User user = userRepository.findById(authUser.getUserId()).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, User.class.getSimpleName())
        );

        // the end time has to be greater than the start time by one hour

        Meeting meeting = new Meeting(request, user);
        meetingRepository.save(meeting);

        // participant 에 추가
        participantService.add(authUser, meeting.getId());
        return ("번개 생성 성공");
    }

    @Transactional
    public String update(AuthUser authUser, MeetingCreateEditRequest request, Long meetingId) {
        // 업로드한 번개 일정 찾기
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, Meeting.class.getSimpleName())
        );
        
        // 작성자가 맞는지 체크
        if (authUser.getUserId()!=meeting.getUser().getId()){
            throw new CustomException(ErrorCode.NO_AUTHORITY, Meeting.class.getSimpleName());
        }
        
        meeting.edit(request);
        return "번개 수정 성공";
    }

    @Transactional
    public String delete(AuthUser authUser, Long meetingId) {
        // 업로드한 번개 일정 찾기
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, Meeting.class.getSimpleName())
        );

        // 작성자가 맞는지 체크
        if (authUser.getUserId()!=meeting.getUser().getId()){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        meeting.markDeleted();
        return "번개 삭제 성공";
    }

    public MeetingDetailResponse get(Long meetingId) {
        // 번개 일정 찾기
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, Meeting.class.getSimpleName())
        );

        int numberJoined = participantService.getJoinedNumber(meeting.getId());
        return new MeetingDetailResponse(meeting, numberJoined);
    }

    public MyMeetingDetailResponse getMyMeeting(Long meetingId) {
        // 번개 일정 찾기
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, Meeting.class.getSimpleName())
        );

        // 번개 신청자들 중 APPROVE 된 신청자들 수
        int numberJoined = participantService.getJoinedNumber(meeting.getId());

        // 번개 신청자들 중 PENDING 상태인 것들 모으기
        List<ParticipantResponse> pendingParticipants = participantService.getPendingParticipants(meeting);

        return new MyMeetingDetailResponse(meeting, numberJoined, pendingParticipants);
    }

    public MyMeetingResponses searchMyMeetings(AuthUser authUser, ParticipantRole role) {
        List<Participant> participantList = new ArrayList<>();
        List<MyMeetingResponse> responseList = new ArrayList<>();
        participantList = participantRepository.findByUserIdAndRole(authUser.getUserId(), role);

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

    public Page<MeetingSearchResponse> search(SortType sortType, Ranks rank, int page, int size) {
        Pageable pageable = PageRequest.of(page -1, size);
        return meetingQueryDslRepository.search(sortType, rank, pageable);
    }

    public String updateMyMeeting(Long meetingId) {
        // 번개 일정 찾기
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, Meeting.class.getSimpleName())
        );

        meeting.markFinished();
        return "번개 완료 성공";
    }
}
