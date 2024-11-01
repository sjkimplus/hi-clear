package com.play.hiclear.domain.participant.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateEditRequest;
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
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.play.hiclear.common.enums.Ranks.RANK_A;
import static com.play.hiclear.domain.user.entity.QUser.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ParticipantServiceTest {
    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MeetingRepository meetingRepository;


    @InjectMocks
    private ParticipantService participantService;

    private AuthUser authUser1;
    private AuthUser authUser2;
    private User user1;
    private User user2;
    private Meeting meeting;
    private Participant participant;
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startTime = now.plusHours(25);
    LocalDateTime endTime = startTime.plusHours(2);
    MeetingCreateEditRequest request = new MeetingCreateEditRequest("title", "region", "content", startTime, endTime, Ranks.RANK_A, 12);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authUser1 = new AuthUser(1L, "일반회원", "test123@gmail.com", UserRole.USER);
        user1 = new User(authUser1.getName(), authUser1.getEmail(), "서울특별시", "encodedPassword", RANK_A, UserRole.USER);
        ReflectionTestUtils.setField(user1, "id", 1L);
        ReflectionTestUtils.setField(authUser1, "userId", 1L);

        authUser2 = new AuthUser(2L, "일반회원", "test123@gmail.com", UserRole.USER);
        user2 = new User(authUser2.getName(), authUser2.getEmail(), "서울특별시", "encodedPassword", RANK_A, UserRole.USER);
        ReflectionTestUtils.setField(user2, "id", 2L);
        ReflectionTestUtils.setField(authUser2, "userId", 2L);

        meeting = new Meeting(request, user1);
        participant = new Participant(meeting, user2, ParticipantRole.GUEST, ParticipantStatus.PENDING);
    }

    @Test
    void add_success() {
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user1.getId())).thenReturn(user1);
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(meeting);
        when(participantRepository.findByMeetingAndUser(meeting, user1)).thenReturn(Optional.empty());

        String result = participantService.add(authUser1, meeting.getId());
        assertEquals("참여자 신청 성공", result);
    }

    @Test
    void add_fail_duplicate_participant() {
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user1.getId())).thenReturn(user1);
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(meeting);
        when(participantRepository.findByMeetingAndUser(meeting, user1)).thenReturn(Optional.of(participant));

        CustomException exception = assertThrows(CustomException.class, () -> {
            participantService.add(authUser1, meeting.getId());
        });

        assertEquals(ErrorCode.DUPLICATE_PARTICIPANT, exception.getErrorCode());
    }

    @Test
    void getJoinedNumber_success() {
        Long meetingId = meeting.getId();
        int expectedCount = 3;

        when(participantRepository.countByMeetingId(meetingId)).thenReturn(expectedCount);

        int result = participantService.getJoinedNumber(meetingId);
        assertEquals(expectedCount, result);
    }

    @Test
    void getPendingParticipants_success() {

        List<Participant> participants = new ArrayList<>();
        participants.add(participant);

        when(participantRepository.findByMeetingAndStatus(meeting, ParticipantStatus.PENDING)).thenReturn(participants);

        List<ParticipantResponse> result = participantService.getPendingParticipants(meeting);

        assertEquals(1, result.size());
        assertEquals(participant.getUser().getName(), result.get(0).getName());
    }

    @Test
    void update_success_guest() {
        ParticipantUpdateRequest request = new ParticipantUpdateRequest(ParticipantStatus.CANCELED);

        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(meeting);
        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

        String result = participantService.update(authUser2, meeting.getId(), participant.getId(), request);

        assertEquals("참여자 status 수정 성공", result);
        assertEquals(ParticipantStatus.CANCELED, participant.getStatus());
    }

    @Test
    void update_fail_too_late() {
        ParticipantUpdateRequest request = new ParticipantUpdateRequest(ParticipantStatus.CANCELED);

        // 시작까지 24시간 이하로 남은 경우
        MeetingCreateEditRequest lateRequest = new MeetingCreateEditRequest("title", "region", "content", now, now.plusHours(1), Ranks.RANK_A, 12);
        Meeting lateMeeting = new Meeting(lateRequest, user2);

        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(lateMeeting);
        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

        CustomException exception = assertThrows(CustomException.class, () -> {
            participantService.update(authUser2, meeting.getId(), participant.getId(), request);
        });

        assertEquals(ErrorCode.TOO_LATE, exception.getErrorCode());
    }

    @Test
    void update_fail_no_authority() {
        ParticipantUpdateRequest request = new ParticipantUpdateRequest(ParticipantStatus.CANCELED);

        when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.of(meeting));
        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

        CustomException exception = assertThrows(CustomException.class, () -> {
            participantService.update(authUser1, meeting.getId(), participant.getId(), request);
        });

        assertEquals(ErrorCode.NO_AUTHORITY, exception.getErrorCode());
    }

    @Test
    void update_success_host() {
        ParticipantUpdateRequest request = new ParticipantUpdateRequest(ParticipantStatus.ACCEPTED);

        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(meeting);
        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

        String result = participantService.update(authUser1, meeting.getId(), participant.getId(), request);

        assertEquals("참여자 status 수정 성공", result);
        assertEquals(ParticipantStatus.ACCEPTED, participant.getStatus());
    }

    @Test
    void update_fail_no_authority_not_the_host() {
        ParticipantUpdateRequest request = new ParticipantUpdateRequest(ParticipantStatus.ACCEPTED);

        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(meeting);
        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

        CustomException exception = assertThrows(CustomException.class, () -> {
            participantService.update(authUser2, meeting.getId(), participant.getId(), request);
        }); // HOST가 아닌 유저가 신청서 수락시도시

        assertEquals(ErrorCode.NO_AUTHORITY, exception.getErrorCode());
    }

    @Test
    void search_success() {
        List<Participant> participants = new ArrayList<>();
        participants.add(participant);

        when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.of(meeting));
        when(participantRepository.findByMeetingAndStatus(meeting, ParticipantStatus.ACCEPTED)).thenReturn(participants);

        ParticipantListResponse result = participantService.search(meeting.getId());

        assertEquals(1, result.getJoinedParticipants().size());
        assertEquals(participant.getUser().getName(), result.getJoinedParticipants().get(0).getName());
    }

    @Test
    void search_fail_meeting_not_found() {
        when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            participantService.search(meeting.getId());
        });

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }
}
