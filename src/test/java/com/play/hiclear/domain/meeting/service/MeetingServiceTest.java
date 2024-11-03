package com.play.hiclear.domain.meeting.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateEditRequest;
import com.play.hiclear.domain.meeting.dto.response.*;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.meeting.repository.MeetingQueryDslRepository;
import com.play.hiclear.domain.meeting.repository.MeetingRepository;
import com.play.hiclear.domain.participant.entity.Participant;
import com.play.hiclear.domain.participant.enums.ParticipantRole;
import com.play.hiclear.domain.participant.enums.ParticipantStatus;
import com.play.hiclear.domain.participant.repository.ParticipantRepository;
import com.play.hiclear.domain.participant.service.ParticipantService;
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
import java.util.List;

import static com.play.hiclear.common.enums.Ranks.RANK_A;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class MeetingServiceTest {

    @InjectMocks
    private MeetingService meetingService;

    @Mock
    private MeetingRepository meetingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ParticipantService participantService;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private MeetingQueryDslRepository meetingQueryDslRepository;

    private AuthUser authUser;
    private User user;
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime futureTime = now.plusHours(2);
    MeetingCreateEditRequest request = new MeetingCreateEditRequest("title", "region", "content", now, futureTime, Ranks.RANK_A, 12);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authUser = new AuthUser(1L, "일반회원", "test123@gmail.com", UserRole.USER);
        user = new User(authUser.getName(), authUser.getEmail(), "서울특별시", "encodedPassword", RANK_A, UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(authUser, "userId", 1L);
    }

    @Test
    void create_success() {
        Meeting meeting = new Meeting(request, user);

        // when
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId())).thenReturn(user);

        // then
        String result = meetingService.create(authUser, request);

        assertEquals("번개 생성 성공", result);
    }

    @Test
    void create_fail_user_not_found() {
        // when
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId())).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // throws a custom exception when trying to create a meeting
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.create(authUser, request)
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void update_success() {
        Meeting meeting = new Meeting(request, user);
        ReflectionTestUtils.setField(meeting, "id", 1L);

        // when
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(meeting);

        // then
        String result = meetingService.update(authUser, request, meeting.getId());

        assertEquals("번개 수정 성공", result);
    }

    @Test
    void update_fail_meeting_not_found() {
        // when
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(1L)).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.update(authUser, request, 1L)
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void delete_success() {
        Meeting meeting = new Meeting(request, user);
        ReflectionTestUtils.setField(meeting, "id", 1L);

        // when
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(meeting);

        // then
        String result = meetingService.delete(authUser, meeting.getId());

        assertEquals("번개 삭제 성공", result);
    }

    @Test
    void delete_fail_meeting_not_found() {
        // when
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(1L)).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // throws a custom exception when trying to delete a non-existent meeting
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.delete(authUser, 1L)
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void get_success() {
        Meeting meeting = new Meeting(request, user);
        ReflectionTestUtils.setField(meeting, "id", 1L);

        // when
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(meeting);
        when(participantService.getJoinedNumber(meeting)).thenReturn(5);

        // then
        MeetingDetailResponse response = meetingService.get(meeting.getId());

        assertEquals(meeting.getId(), response.getId());
        assertEquals(5, response.getNumberJoined());
    }

    @Test
    void get_fail_meeting_not_found() {
        // when
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(1L)).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // throws a custom exception when trying to get a non-existent meeting
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.get(1L)
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getMyMeeting_success() {
        Meeting meeting = new Meeting(request, user);
        ReflectionTestUtils.setField(meeting, "id", 1L);

        // when
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(meeting);
        when(participantService.getJoinedNumber(meeting)).thenReturn(5);
        when(participantService.getPendingParticipants(meeting)).thenReturn(new ArrayList<>());

        // then
        MyMeetingDetailResponse response = meetingService.getMyMeeting(authUser, meeting.getId());

        assertEquals(meeting.getId(), response.getId());
        assertEquals(5, response.getNumberJoined());
        assertTrue(response.getPendingParticipants().isEmpty());
    }

    @Test
    void getMyMeeting_fail_meeting_not_found() {
        // when
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(1L))
                .thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // throws a custom exception when trying to get a non-existent meeting
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.getMyMeeting(authUser, 1L)
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void searchMyMeetings_success_host() {
        // given
        List<Participant> participants = new ArrayList<>();
        List<MyMeetingResponse> responseList = new ArrayList<>();

        Meeting meeting = new Meeting(request, user);
        Participant participant = new Participant(meeting, user, ParticipantRole.HOST, ParticipantStatus.ACCEPTED);
        ReflectionTestUtils.setField(meeting, "id", 1L);

        participants.add(participant);
        responseList.add(new MyMeetingResponse(meeting));

        // when
        when(participantRepository.findByUserIdAndRoleExcludingFinished(authUser.getUserId(), ParticipantRole.HOST)).thenReturn(participants);

        // then
        MyMeetingResponses response = meetingService.searchMyMeetings(authUser, ParticipantRole.HOST);

        assertEquals(1, response.getList().size());
        assertEquals(meeting.getId(), response.getList().get(0).getId());
    }

    @Test
    void searchMyMeetings_success_guest() {
        // given
        List<Participant> participants = new ArrayList<>();
        List<MyMeetingResponse> responseList = new ArrayList<>();

        Meeting meeting = new Meeting(request, user);
        Participant participant = new Participant(meeting, user, ParticipantRole.GUEST, ParticipantStatus.ACCEPTED);
        ReflectionTestUtils.setField(meeting, "id", 1L);

        participants.add(participant);
        responseList.add(new MyMeetingResponse(meeting, participant.getStatus()));

        // when
        when(participantRepository.findByUserIdAndRoleExcludingFinished(authUser.getUserId(), ParticipantRole.GUEST)).thenReturn(participants);

        // then
        MyMeetingResponses response = meetingService.searchMyMeetings(authUser, ParticipantRole.GUEST);

        assertEquals(1, response.getList().size());
        assertEquals(meeting.getId(), response.getList().get(0).getId());
    }
}
