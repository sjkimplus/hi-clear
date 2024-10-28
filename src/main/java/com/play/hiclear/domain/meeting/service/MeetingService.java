package com.play.hiclear.domain.meeting.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateEditRequest;
import com.play.hiclear.domain.meeting.dto.response.MeetingDetailResponse;
import com.play.hiclear.domain.meeting.dto.response.MyMeetingDetailResponse;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.meeting.repository.MeetingRepository;
import com.play.hiclear.domain.participant.dto.ParticipantResponse;
import com.play.hiclear.domain.participant.repository.ParticipantRepository;
import com.play.hiclear.domain.participant.service.ParticipantService;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;

    @Transactional
    public String create(AuthUser authUser, MeetingCreateEditRequest request) {
        User user = userRepository.findById(authUser.getUserId()).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, "User 객체를")
        );

        // the end time has to be greater than the start time by one hour

        Meeting meeting = new Meeting(request, user);
        if (meeting==null) {
            throw new CustomException(ErrorCode.MEETING_CREATION_FAIL);
        }
        meetingRepository.save(meeting);

        // participant 에 추가
        participantService.add(authUser, meeting.getId());
        return ("번개 생성 성공");
    }

    @Transactional
    public String update(AuthUser authUser, MeetingCreateEditRequest request, Long meetingId) {
        // 업로드한 번개 일정 찾기
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, "Meeting 객체를")
        );
        
        // 작성자가 맞는지 체크
        if (authUser.getUserId()!=meeting.getUser().getId()){
            throw new CustomException(ErrorCode.NO_AUTHORITY, "이 번개에");
        }
        
        meeting.edit(request);
        meetingRepository.save(meeting);
        return "번개 수정 성공";
    }

    @Transactional
    public String delete(AuthUser authUser, Long meetingId) {
        // 업로드한 번개 일정 찾기
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, "Meeting 객체를")
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
                new CustomException(ErrorCode.NOT_FOUND, "Meeting 객체를")
        );

        int numberJoined = participantService.getJoinedNumber(meeting.getId());
        return new MeetingDetailResponse(meeting, numberJoined);
    }

    public MyMeetingDetailResponse getMyMeeting(Long meetingId) {
        // 번개 일정 찾기
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, "Meeting 객체를")
        );

        // 번개 신청자들 중 APPROVE 된 신청자들 수
        int numberJoined = participantService.getJoinedNumber(meeting.getId());

        // 번개 신청자들 중 PENDING 상태인 것들 모으기
        List<ParticipantResponse> pendingParticipants = participantService.getPendingParticipants(meeting);

        return new MyMeetingDetailResponse(meeting, numberJoined, pendingParticipants);
    }
}
