package com.play.hiclear.domain.schedule.service;

import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import com.play.hiclear.domain.clubmember.enums.ClubMemberRole;
import com.play.hiclear.domain.schduleparticipant.entity.ScheduleParticipant;
import com.play.hiclear.domain.schduleparticipant.repository.ScheduleParticipantRepository;
import com.play.hiclear.domain.schedule.dto.request.ScheduleRequest;
import com.play.hiclear.domain.schedule.dto.response.ScheduleSearchDetailResponse;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.schedule.repository.ScheduleRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.play.hiclear.common.enums.Ranks.RANK_A;
import static com.play.hiclear.common.enums.Ranks.RANK_B;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScheduleParticipantRepository scheduleParticipantRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private User admin;
    private User user;
    private Club club;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        admin = new User(1L, "John Doe", "john@example.com", "전라북도 익산시", RANK_A, UserRole.BUSINESS);
        user = new User(2L, "Jamee", "jamee@example.com", "경기도 익산시", RANK_B, UserRole.USER);

        club = new Club(1L, admin, "Test Club", 10, "A great club", "Seoul", "secret", new ArrayList<>());
        club.getClubMembers().add(new ClubMember(1L, user, club, ClubMemberRole.ROLE_MEMBER));
        club.getClubMembers().add(new ClubMember(2L, admin, club, ClubMemberRole.ROLE_ADMIN));

        schedule = new Schedule(1L, "Test Schedule Title", "This is a test description.", "Seoul",
                LocalDateTime.of(2024, 10, 1, 10, 0), LocalDateTime.of(2024, 10, 1, 12, 0),
                club, new ArrayList<>(), admin);

        // User Mock 설정
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user)); // User Mock 추가
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin)); // Admin Mock 추가
    }

    @Test
    void createSchedule_success() {
        // Given
        ScheduleRequest request = new ScheduleRequest("Test Schedule Title", "This is a test description.", "Seoul",
                LocalDateTime.of(2024, 10, 1, 10, 0), LocalDateTime.of(2024, 10, 1, 12, 0),
                List.of(user.getId())); // user ID 추가

        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user)); // User Mock 추가
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin)); // Admin Mock 추가
        when(clubRepository.findById(club.getId())).thenReturn(Optional.of(club));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);
        when(scheduleParticipantRepository.existsByScheduleAndUser(any(Schedule.class), any(User.class))).thenReturn(false); // Mock 설정 추가

        // When
        ScheduleSearchDetailResponse result = scheduleService.create(admin.getEmail(), club.getId(), request);

        // Then
        assertNotNull(result);
        assertEquals(schedule.getTitle(), result.getTitle());
        assertEquals(schedule.getDescription(), result.getDescription());
        assertEquals(schedule.getRegion(), result.getRegion());
        assertEquals(schedule.getStartTime(), result.getStartTime());
        assertEquals(schedule.getEndTime(), result.getEndTime());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void get() {
        // Given
        when(scheduleRepository.findById(schedule.getId())).thenReturn(Optional.of(schedule));
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        // When
        ScheduleSearchDetailResponse result = scheduleService.get(schedule.getId(), admin.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(schedule.getTitle(), result.getTitle());
        assertEquals(schedule.getDescription(), result.getDescription());
        verify(scheduleRepository, times(1)).findById(schedule.getId());
    }

    @Test
    void search() {
        // Given
        when(clubRepository.findById(club.getId())).thenReturn(Optional.of(club));
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(scheduleRepository.findByClubAndDeletedAtIsNull(club)).thenReturn(List.of(schedule));

        // When
        List<Schedule> result = scheduleService.search(club.getId(), admin.getEmail());

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(schedule.getTitle(), result.get(0).getTitle());
    }

    @Test
    void update() {
        // Given
        ScheduleRequest request = new ScheduleRequest("Updated Title", "Updated description", "Updated region",
                LocalDateTime.of(2024, 10, 1, 11, 0), LocalDateTime.of(2024, 10, 1, 13, 0),
                List.of(user.getId()));

        when(scheduleRepository.findById(schedule.getId())).thenReturn(Optional.of(schedule));
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        // When
        ScheduleSearchDetailResponse result = scheduleService.update(schedule.getId(), request, admin.getEmail());

        // Then
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated description", result.getDescription());
        verify(scheduleRepository, times(1)).findById(schedule.getId());
    }

    @Test
    void delete() {
        // Given
        when(scheduleRepository.findById(schedule.getId())).thenReturn(Optional.of(schedule));
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        // When
        scheduleService.delete(schedule.getId(), admin.getEmail());

        // Then
        verify(scheduleRepository, times(1)).findById(schedule.getId());
        verify(scheduleRepository, times(1)).save(schedule);
    }
}