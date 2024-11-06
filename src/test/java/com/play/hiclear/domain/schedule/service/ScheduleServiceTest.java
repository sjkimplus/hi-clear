package com.play.hiclear.domain.schedule.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import com.play.hiclear.domain.clubmember.enums.ClubMemberRole;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        admin = new User(1L, "John Doe", "john@example.com", "서울 노원구", RANK_A, UserRole.BUSINESS);
        user = new User(2L, "Jamee", "jamee@example.com", "경기도 수원시", RANK_B, UserRole.USER);

        club = new Club(1L, admin, "Test Club", 10, "A great club", "Seoul", "secret", new ArrayList<>());
        club.getClubMembers().add(new ClubMember(1L, user, club, ClubMemberRole.ROLE_MEMBER));
        club.getClubMembers().add(new ClubMember(2L, admin, club, ClubMemberRole.ROLE_MASTER));

        // Schedule 객체 초기화
        schedule = new Schedule(1L, "Test Schedule Title", "This is a test description.", "Seoul",
                LocalDateTime.of(2024, 10, 1, 10, 0), LocalDateTime.of(2024, 10, 1, 12, 0),
                club, new ArrayList<>(), admin);

        authUser = new AuthUser(admin.getId(), admin.getName(), admin.getEmail(), UserRole.BUSINESS);

        // Mock 설정
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(admin);
        when(scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(schedule.getId())).thenReturn(schedule);
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user.getId())).thenReturn(user);
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(admin.getId())).thenReturn(admin);
        when(clubRepository.findByIdAndDeletedAtIsNullOrThrow(club.getId())).thenReturn(club);
    }


    @Test
    void create_success() {
        // Given
        ScheduleRequest request = new ScheduleRequest("Test Schedule Title", "This is a test description.", "Seoul",
                LocalDateTime.of(2024, 10, 1, 10, 0), LocalDateTime.of(2024, 10, 1, 12, 0),
                List.of(user.getId()));

        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(admin.getEmail())).thenReturn(admin);
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user.getId())).thenReturn(user);
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(admin.getId())).thenReturn(admin);
        when(clubRepository.findByIdAndDeletedAtIsNullOrThrow(club.getId())).thenReturn(club);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);
        when(scheduleParticipantRepository.existsByScheduleAndUser(any(Schedule.class), any(User.class))).thenReturn(false); // Mock 설정 추가

        // When
        ScheduleSearchDetailResponse result = scheduleService.create(authUser, club.getId(), request);

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
    void create_fail_notClubMember() {
        // Given
        ScheduleRequest request = new ScheduleRequest("Test Schedule Title", "This is a test description.", "Seoul",
                LocalDateTime.of(2024, 10, 1, 10, 0), LocalDateTime.of(2024, 10, 1, 12, 0),
                List.of(user.getId()));

        // user는 클럽의 회원이 아님
        club.getClubMembers().removeIf(member -> member.getUser().getId().equals(user.getId()));

        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(admin);
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user.getId())).thenReturn(user);
        when(clubRepository.findByIdAndDeletedAtIsNullOrThrow(club.getId())).thenReturn(club);

        // user가 클럽 회원이 아니라고 명시
        when(scheduleParticipantRepository.existsByScheduleAndUser(any(Schedule.class), eq(user))).thenReturn(false);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.create(authUser, club.getId(), request);
        });
        assertEquals(ErrorCode.SCHEDULE_NOT_A_CLUB_MEMBER, exception.getErrorCode());
    }

    @Test
    void create_fail_scheduleAlreadyExists() {
        // Given
        ScheduleRequest request = new ScheduleRequest("Test Schedule Title", "This is a test description.", "Seoul",
                LocalDateTime.of(2024, 10, 1, 10, 0), LocalDateTime.of(2024, 10, 1, 12, 0),
                List.of(user.getId()));

        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(admin);
        when(clubRepository.findByIdAndDeletedAtIsNullOrThrow(club.getId())).thenReturn(club);

        // 중복 스케줄 존재로 Mock 설정
        when(scheduleRepository.existsByClubIdAndStartTimeAndTitleAndDeletedAtIsNull(eq(club.getId()), eq(request.getStartTime()), eq(request.getTitle())))
                .thenReturn(true); // 중복 스케줄 존재

        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user.getId())).thenReturn(user);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.create(authUser, club.getId(), request);
        });
        assertEquals(ErrorCode.DUPLICATE_SCHEDULE, exception.getErrorCode());
    }

    @Test
    void create_fail_invalidScheduleTime() {
        // Given
        ScheduleRequest request = new ScheduleRequest("Test Schedule Title", "This is a test description.", "Seoul",
                LocalDateTime.of(2024, 10, 1, 12, 0), // 시작 시간이 종료 시간보다 늦음
                LocalDateTime.of(2024, 10, 1, 10, 0), // 종료 시간
                List.of(user.getId()));

        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(admin);
        when(clubRepository.findByIdAndDeletedAtIsNullOrThrow(club.getId())).thenReturn(club);

        User participantUser = new User(user.getId(), user.getName(), user.getEmail(), user.getAddress(), user.getSelfRank(), user.getUserRole());
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(user.getId())).thenReturn(participantUser);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.create(authUser, club.getId(), request);
        });
        assertEquals(ErrorCode.INVALID_SCHEDULE_TIME, exception.getErrorCode());
    }


    @Test
    void get_success() {
        // Given
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(admin.getEmail())).thenReturn(admin);
        when(scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(schedule.getId())).thenReturn(schedule);

        // When
        ScheduleSearchDetailResponse result = scheduleService.get(schedule.getId(), authUser);

        // Then
        assertNotNull(result);
        assertEquals(schedule.getTitle(), result.getTitle());
        assertEquals(schedule.getDescription(), result.getDescription());
        verify(userRepository, times(1)).findByEmailAndDeletedAtIsNullOrThrow(admin.getEmail());
        verify(scheduleRepository, times(1)).findByIdAndDeletedAtIsNullOrThrow(schedule.getId());
    }

    @Test
    void get_fail_userDeleted() {
        // Given
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(admin.getEmail())).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.get(schedule.getId(), authUser);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void get_fail_scheduleDeleted() {
        // Given
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(admin.getEmail())).thenReturn(admin);
        when(scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(schedule.getId())).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.get(schedule.getId(), authUser);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void get_fail_notClubMember() {
        // Given
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(admin.getEmail())).thenReturn(admin);
        when(scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(schedule.getId())).thenReturn(schedule);
        when(clubRepository.findByIdAndDeletedAtIsNullOrThrow(schedule.getClub().getId())).thenReturn(club);

        // 클럽 멤버가 아님을 시뮬레이션
        // 여기서 클럽의 멤버 리스트를 비우거나 특정 멤버가 없음을 설정
        club.getClubMembers().clear(); // 클럽에 멤버가 없음

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.get(schedule.getId(), authUser);
        });
        assertEquals(ErrorCode.SCHEDULE_NOT_A_CLUB_MEMBER, exception.getErrorCode());
    }

    @Test
    void search_success() {
        // Given
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(admin);
        when(clubRepository.findByIdAndDeletedAtIsNullOrThrow(club.getId())).thenReturn(club);

        List<Schedule> schedulesList = List.of(schedule);
        Page<Schedule> schedulesPage = new PageImpl<>(schedulesList, PageRequest.of(0, 10), schedulesList.size());

        when(scheduleRepository.findAllByClubAndDeletedAtIsNullAndFilters(eq(club), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class))).thenReturn(schedulesPage);

        // When
        Page<Schedule> result = scheduleService.search(club.getId(), authUser, 1, 10, null, null, null, null, null);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(schedule.getTitle(), result.getContent().get(0).getTitle());
    }

    @Test
    void search_fail_userNotClubMember() {
        // Given
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(admin);
        when(clubRepository.findByIdAndDeletedAtIsNullOrThrow(club.getId())).thenReturn(club);

        // 클럽에 사용자가 멤버가 아님으로 설정
        club.getClubMembers().clear();

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.search(club.getId(), authUser, 1, 10, null, null, null, null, null);
        });

        assertEquals(ErrorCode.SCHEDULE_NOT_A_CLUB_MEMBER, exception.getErrorCode());
    }

    @Test
    void search_fail_clubNotFound() {
        // Given
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(admin);
        when(clubRepository.findByIdAndDeletedAtIsNullOrThrow(club.getId())).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.search(club.getId(), authUser, 1, 10, null, null, null, null, null);
        });

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }


    @Test
    void update_success() {
        // Given
        Long scheduleId = schedule.getId();
        ScheduleRequest updateRequest = new ScheduleRequest(
                "Updated Title",
                "Updated Description",
                "Updated Region",
                LocalDateTime.of(2024, 10, 1, 11, 0),
                LocalDateTime.of(2024, 10, 1, 13, 0),
                List.of(user.getId())
        );

        // Mocking: 스케줄 조회
        when(scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(scheduleId)).thenReturn(schedule);

        // Mocking: 스케줄 저장
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // When
        ScheduleSearchDetailResponse result = scheduleService.update(scheduleId, updateRequest, authUser);

        // Then
        assertNotNull(result);
        assertEquals(updateRequest.getTitle(), result.getTitle());
        assertEquals(updateRequest.getDescription(), result.getDescription());
        assertEquals(updateRequest.getRegion(), result.getRegion());
        assertEquals(updateRequest.getStartTime(), result.getStartTime());
        assertEquals(updateRequest.getEndTime(), result.getEndTime());

        // Verify: 저장 호출 확인
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void update_fail_scheduleNotFound() {
        // Given
        Long nonExistentScheduleId = 999L; // 존재하지 않는 스케줄 ID
        ScheduleRequest updateRequest = new ScheduleRequest(
                "Updated Title",
                "Updated Description",
                "Updated Region",
                LocalDateTime.of(2024, 10, 1, 11, 0),
                LocalDateTime.of(2024, 10, 1, 13, 0),
                List.of(user.getId())
        );

        // Mocking: 존재하지 않는 스케줄 조회
        when(scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(nonExistentScheduleId))
                .thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.update(nonExistentScheduleId, updateRequest, authUser);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }



    @Test
    void delete_success() {
        // Given
        AuthUser authUser = new AuthUser(admin.getId(), admin.getName(), admin.getEmail(), UserRole.BUSINESS);

        // Schedule이 존재하는 경우
        when(scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(schedule.getId())).thenReturn(schedule);
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(admin);
        when(scheduleParticipantRepository.findBySchedule(schedule)).thenReturn(new ArrayList<>()); // 참여자가 없다고 가정

        // When
        scheduleService.delete(schedule.getId(), authUser);

        // Then
        verify(scheduleRepository, times(1)).findByIdAndDeletedAtIsNullOrThrow(schedule.getId());
        verify(scheduleParticipantRepository, times(1)).findBySchedule(schedule);
    }

    @Test
    void delete_fail_scheduleNotFound() {
        // Given
        Long nonExistentScheduleId = 999L; // 존재하지 않는 스케줄 ID
        AuthUser authUser = new AuthUser(admin.getId(), admin.getName(), admin.getEmail(), UserRole.BUSINESS);

        // Mocking: 존재하지 않는 스케줄 조회
        when(scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(nonExistentScheduleId))
                .thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            scheduleService.delete(nonExistentScheduleId, authUser);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

}