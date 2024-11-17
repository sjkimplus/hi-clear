package com.play.hiclear.domain.meeting.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateRequest;
import com.play.hiclear.domain.meeting.dto.request.MeetingUpdateRequest;
import com.play.hiclear.domain.meeting.dto.response.MeetingDetailResponse;
import com.play.hiclear.domain.meeting.dto.response.MyMeetingDetailResponse;
import com.play.hiclear.domain.meeting.dto.response.MyMeetingResponse;
import com.play.hiclear.domain.meeting.dto.response.MyMeetingResponses;
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
    @Mock
    private GeoCodeService geoCodeService;

    private AuthUser authUser;
    private User user;
    private MeetingCreateRequest request;
    private GeoCodeDocument geoCodeDocument;
    private Meeting meeting;

    private final LocalDateTime startTime = LocalDateTime.now().plusHours(1);
    private final LocalDateTime endTime = startTime.plusHours(2);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        authUser = new AuthUser(1L, "일반회원", "test123@gmail.com", UserRole.USER);
        user = new User("일반회원", "test123@gmail.com", "서울 관악구 신림동 533-29",
                "서울 관악구 조원로 89-1", null, "encodedPassword", Ranks.RANK_A, UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        request = new MeetingCreateRequest("title", "서울 관악구 신림동 533-29", "content", startTime, endTime, Ranks.RANK_A, 12);

        geoCodeDocument = new GeoCodeDocument(37.5665, 126.9780, "서울 관악구 신림동 533-29", "서울 관악구 조원로 89-1");

        meeting = new Meeting(request, user, geoCodeDocument);
        ReflectionTestUtils.setField(meeting, "id", 1L);
    }

    @Test
    void create_success() {
        // when
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId())).thenReturn(user);
        when(geoCodeService.getGeoCode(request.getAddress())).thenReturn(geoCodeDocument);
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);

        // then
        String result = meetingService.create(authUser, request);

        // result
        assertEquals(SuccessMessage.customMessage(SuccessMessage.CREATED, Meeting.class.getSimpleName()), result);
    }

    @Test
    void create_fail_user_not_found() {
        // when
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId())).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // then
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.create(authUser, request)
        );

        // result
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void update_success() {
        MeetingUpdateRequest updateRequest = new MeetingUpdateRequest("updatedTitle", null, null,
                null, null, null, 12);

        // when
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(meeting);

        // then
        String result = meetingService.update(authUser, updateRequest, meeting.getId());

        assertEquals(SuccessMessage.customMessage(SuccessMessage.MODIFIED, Meeting.class.getSimpleName()), result);
    }

    @Test
    void update_fail_meeting_not_found() {
        MeetingUpdateRequest updateRequest = new MeetingUpdateRequest("updatedTitle", null, null,
                null, null, null, 12);

        // when
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(1L)).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // then
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.update(authUser, updateRequest, 1L)
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void delete_success() {

        // when
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(meeting);

        // then
        String result = meetingService.delete(authUser, meeting.getId());

        assertEquals(SuccessMessage.customMessage(SuccessMessage.DELETED, Meeting.class.getSimpleName()), result);
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

        // then
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.get(1L)
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getMyMeeting_success() {
        // when
        when(meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId())).thenReturn(meeting);
        when(participantService.getJoinedNumber(meeting)).thenReturn(0);
        when(participantService.getPendingParticipants(meeting)).thenReturn(new ArrayList<>());

        // then
        MyMeetingDetailResponse response = meetingService.getMyMeeting(authUser, meeting.getId());

        assertEquals(meeting.getId(), response.getId());
        assertEquals(0, response.getNumberJoined());
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
