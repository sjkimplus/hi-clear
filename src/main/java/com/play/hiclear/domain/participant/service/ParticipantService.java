package com.play.hiclear.domain.participant.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.meeting.repository.MeetingRepository;
import com.play.hiclear.domain.participant.entity.Participant;
import com.play.hiclear.domain.participant.enums.ParticipantRole;
import com.play.hiclear.domain.participant.enums.ParticipantStatus;
import com.play.hiclear.domain.participant.repository.ParticipantRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    public String add(AuthUser authUser, Long meetingId) {

        User user = userRepository.findById(authUser.getUserId()).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, "User 객체를")
        );

        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, "Meeting 객체를")
        );

        // role: HOST인지 GUEST인지 확인
        ParticipantRole role;
        ParticipantStatus status;
        if (meeting.getUser().getId()==user.getId()) {
            role = ParticipantRole.HOST;
            status = ParticipantStatus.ACCEPTED;
        } else {
            role = ParticipantRole.GUEST;
            status = ParticipantStatus.PENDING;
        }

        Participant participant = new Participant(meeting, user, role, status);
        participantRepository.save(participant);
        return "참여자 신청 성공";
    }
}
