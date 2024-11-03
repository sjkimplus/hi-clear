package com.play.hiclear.domain.participant.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.meeting.repository.MeetingRepository;
import com.play.hiclear.domain.participant.dto.ParticipantListResponse;
import com.play.hiclear.domain.participant.dto.ParticipantResponse;
import com.play.hiclear.domain.participant.dto.ParticipantUpdateRequest;
import com.play.hiclear.domain.participant.entity.Participant;
import com.play.hiclear.domain.participant.enums.ParticipantRole;
import com.play.hiclear.domain.participant.enums.ParticipantStatus;
import com.play.hiclear.domain.participant.repository.ParticipantRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    /**
     * 참여 신청
     * @param authUser
     * @param meetingId
     * @return
     */
    @Transactional
    public String add(AuthUser authUser, Long meetingId) {

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        // 이미 추가된 유저인 경우 중복추가 방지
        Optional<Participant> existingParticipant = participantRepository.findByMeetingAndUser(meeting, user);
        if (existingParticipant.isPresent()){
            Participant participant = existingParticipant.get();
            // 취소가 아닌 다른 경우는 에러처리
            if (participant.getStatus() != ParticipantStatus.CANCELED) {
                throw new CustomException(ErrorCode.DUPLICATE_PARTICIPANT);
            }
            // 전에 취소 했던 경우 다시 신청 가능. 단, update 메서드로 status만 바꿈
            ParticipantUpdateRequest request = new ParticipantUpdateRequest(ParticipantStatus.PENDING);
            update(authUser, meetingId, participant.getId(), request);
            return "참여자 신청 성공";
        }

        // role: HOST인지 GUEST인지 정하기
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

    /**
     * 확정된 참여자 수 가져오기
     * @param meeting
     * @return
     */
    public int getJoinedNumber(Meeting meeting) {
        return participantRepository.countByMeetingAndStatus(meeting, ParticipantStatus.ACCEPTED); // 참여확정자만으로 수정
    }

    public List<ParticipantResponse> getPendingParticipants(Meeting meeting) {

        List<Participant> pendingUsers = participantRepository.findByMeetingAndStatus(meeting, ParticipantStatus.PENDING);
        return pendingUsers.stream()
                .map(ParticipantResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 번개 참여 신청 철회/거절/승락
     * @param authUser
     * @param meetingId
     * @param participantId
     * @param request
     * @return
     */
    @Transactional
    public String update(AuthUser authUser, Long meetingId, Long participantId, ParticipantUpdateRequest request) {
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meetingId);

        Participant participant = participantRepository.findById(participantId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, Participant.class.getSimpleName())
        );

        // 번개 신청 철회 - 신청자만 가능 CANCEL
        if (request.getStatus()==ParticipantStatus.CANCELED) {
            // 본인 확인
            if (participant.getUser().getId()!=authUser.getUserId()) {
                throw new CustomException(ErrorCode.NO_AUTHORITY, Participant.class.getSimpleName());
            }
            // 번개 시작까지 24시간 이상 남았는지 확인
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(meeting.getStartTime().minusHours(24))) {
                throw new CustomException(ErrorCode.TOO_LATE);
            }
            participant.updateStatus(request.getStatus());
        } else if (request.getStatus()==ParticipantStatus.ACCEPTED || request.getStatus()==ParticipantStatus.REJECTED) {
            // 번개 거절/승락 - 개최자만 가능 ACCEPT/REJECT
            // 본인 확인
            if (meeting.getUser().getId()!=authUser.getUserId()) {
                throw new CustomException(ErrorCode.NO_AUTHORITY, Participant.class.getSimpleName());
            }
            participant.updateStatus(request.getStatus());
        } else { // 기타는 불가
            throw new CustomException(ErrorCode.WRONG_STATUS);
        }
        return "참여자 status 수정 성공";
    }

    /**
     * 번개 참여자 리스트 조회
     * @param meetingId
     * @return
     */
    public ParticipantListResponse search(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, Meeting.class.getSimpleName())
        );
        List<Participant> joinedParticipants = participantRepository.findByMeetingAndStatus(meeting, ParticipantStatus.ACCEPTED);
        List<ParticipantResponse> list = new ArrayList<>();
        for (Participant p : joinedParticipants) {
            list.add(new ParticipantResponse(p));
        }
        return new ParticipantListResponse(list);
    }
}
