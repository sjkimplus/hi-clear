package com.play.hiclear.domain.timeslot.sevice;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.repository.CourtRepository;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.timeslot.dto.request.TimeSlotRequest;
import com.play.hiclear.domain.timeslot.dto.response.TimeSlotResponse;
import com.play.hiclear.domain.timeslot.dto.response.TimeSlotSimpleResponse;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.timeslot.repository.TimeSlotRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimeSlotServiceTest {

    @InjectMocks
    TimeSlotService timeSlotService;

    @Mock
    TimeSlotRepository timeSlotRepository;

    @Mock
    GymRepository gymRepository;

    @Mock
    CourtRepository courtRepository;

    private AuthUser authUser;
    private User user;
    private Gym gym;
    private Court court;

    @BeforeEach
    void setup() {
        authUser = new AuthUser(1L, "사업자1", "test1@gmail.com", UserRole.BUSINESS);
        user = new User(authUser.getName(), authUser.getEmail(), "서울특별시", "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        ReflectionTestUtils.setField(user, "id", 1L);
        gym = new Gym("공공체육관1", "공공체육관 설명1", "서울특별시", GymType.PUBLIC, user);
        ReflectionTestUtils.setField(gym, "id", 1L);
        when(gymRepository.findByIdAndDeletedAtIsNullOrThrow(1L)).thenReturn(gym);
        court = new Court(1L, 10000, gym);
        when(courtRepository.findByCourtNumAndGymIdOrThrow(1L, 1L)).thenReturn(court);
    }


    @Test
    void create_success() {
        // given
        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(LocalTime.of(10, 00));
        when(timeSlotRepository.findByStartTimeAndCourt_CourtNum(timeSlotRequest.getStartTime(), 1L))
                .thenReturn(Optional.empty());

        // when
        TimeSlotResponse result = timeSlotService.create(authUser, 1L, 1L, timeSlotRequest);

        // then
        assertEquals(LocalTime.of(10, 00), result.getStartTime());
    }


    @Test
    void create_fail_duplicated_startTime() {
        // given
        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(LocalTime.of(10, 00));
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(10, 00), 1L, court);
        when(timeSlotRepository.findByStartTimeAndCourt_CourtNum(timeSlotRequest.getStartTime(), 1L))
                .thenReturn(Optional.of(timeSlot));

        // when && then
        CustomException exception = assertThrows(CustomException.class, () -> {
            timeSlotService.create(authUser, 1L, 1L, timeSlotRequest);
        });
    }


    @Test
    void search_success() {
        // given
        TimeSlot timeSlot1 = new TimeSlot(LocalTime.of(10, 00), 1L, court);
        TimeSlot timeSlot2 = new TimeSlot(LocalTime.of(11, 00), 1L, court);
        TimeSlot timeSlot3 = new TimeSlot(LocalTime.of(12, 00), 1L, court);
        List<TimeSlot> timeSlotList = Arrays.asList(timeSlot1, timeSlot2, timeSlot3);
        when(timeSlotRepository.findAllByCourt_CourtNum(1L)).thenReturn(timeSlotList);

        // when
        List<TimeSlotSimpleResponse> results = timeSlotService.search(authUser, 1L, 1L);

        // then
        assertEquals(3, results.size());
        assertEquals(timeSlot1.getStartTime(), results.get(0).getStartTime());
        assertEquals(timeSlot2.getStartTime(), results.get(1).getStartTime());
        assertEquals(timeSlot3.getStartTime(), results.get(2).getStartTime());
    }


    @Test
    void delete_success() {
        // given
        TimeSlotRequest timeSlotRequest = new TimeSlotRequest(LocalTime.of(10, 00));
        TimeSlot timeSlot = new TimeSlot(timeSlotRequest.getStartTime(), 1L, court);
        when(timeSlotRepository.findByStartTimeAndCourt_CourtNumOrThrow(timeSlotRequest.getStartTime(), 1L))
                .thenReturn(timeSlot);

        // when
        timeSlotService.delete(authUser, 1L, 1L, timeSlotRequest);


        // then
        verify(timeSlotRepository).delete(timeSlot);
        when(timeSlotRepository.findByStartTimeAndCourt_CourtNum(timeSlotRequest.getStartTime(), 1L))
                .thenReturn(Optional.empty());
        Optional<TimeSlot> result = timeSlotRepository.findByStartTimeAndCourt_CourtNum(timeSlotRequest.getStartTime(), 1L);
        assertTrue(result.isEmpty());
    }
}