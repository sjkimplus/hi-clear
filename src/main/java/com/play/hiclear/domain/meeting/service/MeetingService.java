package com.play.hiclear.domain.meeting.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateEditRequest;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.meeting.repository.MeetingRepository;
import com.play.hiclear.domain.participant.service.ParticipantService;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final ParticipantService participantService;

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

    public String update(AuthUser authUser, MeetingCreateEditRequest request, Long meetingId) {
        // 업로드한 번개 일정 찾기
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, "Meeting 객체를")
        );
        
        // 작성자가 맞는지 체크
        if (authUser.getUserId()!=meeting.getUser().getId()){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }
        
        meeting.edit(request);
        meetingRepository.save(meeting);
        return "번개 수정 성공";
    }

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
        meetingRepository.save(meeting);
        return "번개 삭제 성공";
    }


//    public MeetingSearchDetailResponse searchMeeting(AuthUser authUser) {
//
//    }
}
