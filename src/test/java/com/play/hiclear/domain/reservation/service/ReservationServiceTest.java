package com.play.hiclear.domain.reservation.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.repository.CourtRepository;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.reservation.dto.request.ReservationChangeStatusRequest;
import com.play.hiclear.domain.reservation.dto.request.ReservationRequest;
import com.play.hiclear.domain.reservation.dto.request.ReservationUpdateRequest;
import com.play.hiclear.domain.reservation.dto.response.ReservationSearchDetailResponse;
import com.play.hiclear.domain.reservation.dto.response.ReservationSearchResponse;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.reservation.repository.ReservationRepository;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.timeslot.repository.TimeSlotRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.play.hiclear.common.enums.Ranks.RANK_A;
import static com.play.hiclear.common.enums.Ranks.RANK_C;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {
    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GymRepository gymRepository;

    private User admin;
    private User user;
    private Court court;
    private Gym gym;
    private TimeSlot timeSlot1;
    private TimeSlot timeSlot2;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        admin = new User("John Doe", "john@example.com", "서울", RANK_A, UserRole.BUSINESS);
        user = new User("Jame", "jame@example.com", "인천", RANK_C, UserRole.USER);
        gym = new Gym("My Gym", "운동하기 좋은 체육관", "region", "roadAdress", 32.6938482, 45.2938482, GymType.PRIVATE, admin);

        court = new Court(25L, 150000, gym);
        ReflectionTestUtils.setField(court, "id", 1L);

        timeSlot1 = new TimeSlot(LocalTime.of(10, 0) , court.getId(), court);
        ReflectionTestUtils.setField(timeSlot1, "id", 1L);
        timeSlot2 = new TimeSlot(LocalTime.of(12, 0), court.getId(), court);
        ReflectionTestUtils.setField(timeSlot2, "id", 2L);

        // 예약을 생성할 때 사용자 정보도 설정
        reservation = new Reservation(user, court, timeSlot1, ReservationStatus.PENDING, LocalDate.of(2024, 11, 1));
        ReflectionTestUtils.setField(reservation, "id", 1L);
        // Mock 설정
        when(courtRepository.findByIdAndDeletedAtIsNullOrThrow(court.getId())).thenReturn(court);
        when(timeSlotRepository.findAllById(Arrays.asList(timeSlot1.getId(), timeSlot2.getId())))
                .thenReturn(Arrays.asList(timeSlot1, timeSlot2));
    }

    private void setupMocksForCreate(ReservationRequest request) {
        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());

        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(user);
        when(reservationRepository.findByTimeSlotIdInAndStatusIn(anyList(), anyList())).thenReturn(Collections.emptyList());

        // Mock: 예약 저장 호출에 대한 설정
        when(reservationRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Reservation> reservationsToSave = invocation.getArgument(0);
            List<Reservation> savedReservations = new ArrayList<>();

            for (Reservation reservation : reservationsToSave) {
                // user를 설정하여 새로운 예약 생성
                Reservation newReservation = new Reservation(user, reservation.getCourt(), reservation.getTimeSlot(), reservation.getStatus(), reservation.getDate());
                savedReservations.add(newReservation);
            }
            return savedReservations;
        });
    }

    @Test
    void create_success() {
        // Given
        ReservationRequest request = new ReservationRequest(
                Arrays.asList(timeSlot1.getId(), timeSlot2.getId()),
                court.getId(),
                LocalDate.of(2024, 11, 28)
        );

        setupMocksForCreate(request);
        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());

        // When
        List<ReservationSearchDetailResponse> result = reservationService.create(authUser, request);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(reservationRepository, times(1)).saveAll(anyList());
    }

    // 예약 생성 실패 테스트 케이스
    @Test
    void create_fail_userNotFound() {
        // Given
        ReservationRequest request = new ReservationRequest(
                Arrays.asList(timeSlot1.getId(), timeSlot2.getId()),
                court.getId(),
                LocalDate.of(2024, 11, 3)
        );

        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(anyString())).thenThrow(new CustomException(ErrorCode.NOT_FOUND, User.class.getSimpleName()));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.create(new AuthUser(0L, "unknown", "unknown@example.com", UserRole.USER), request);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    // 예약 조회 성공 테스트 케이스
    @Test
    void get_success() {
        // Given
        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());

        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(user);
        when(reservationRepository.findByIdAndDeletedAtIsNullOrThrow(reservation.getId())).thenReturn(reservation);

        // When
        ReservationSearchDetailResponse result = reservationService.get(reservation.getId(), authUser);

        // Then
        assertNotNull(result);
        assertEquals(reservation.getId(), result.getId());
    }

    // 예약 조회 실패 테스트 케이스
    @Test
    void get_fail_userNotFound() {
        // Given: 사용자를 찾을 수 없도록 설정
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(anyString())).thenThrow(new CustomException(ErrorCode.NOT_FOUND, User.class.getSimpleName()));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.get(reservation.getId(), new AuthUser(0L, "unknown", "unknown@example.com", UserRole.USER));
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void get_fail_reservationNotFound() {
        // Given
        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());

        // 예약이 존재하지 않도록 설정
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(user);
        when(reservationRepository.findByIdAndDeletedAtIsNullOrThrow(reservation.getId())).thenThrow(new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.get(reservation.getId(), authUser);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }


    // 예약 목록 조회 성공 테스트 케이스 (비즈니스 사용자)
    @Test
    void search_success_businessUser() {
        // Given
        AuthUser authUser = new AuthUser(admin.getId(), admin.getName(), admin.getEmail(), admin.getUserRole());
        List<Reservation> reservations = Collections.singletonList(reservation);
        Page<Reservation> page = new PageImpl<>(reservations);

        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(admin);
        when(gymRepository.findByUserAndDeletedAtIsNullOrThrow(admin)).thenReturn(gym);
        when(reservationRepository.findByGymUserAndDeletedAtIsNull(eq(gym.getUser()), isNull(), isNull(), isNull(), any(Pageable.class))).thenReturn(page);

        // When
        Page<ReservationSearchResponse> result = reservationService.search(authUser, 1, 10, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(reservation.getId(), result.getContent().get(0).getId());
    }

    // 예약 목록 조회 성공 테스트 케이스 (일반 사용자)
    @Test
    void search_success_regularUser() {
        // Given
        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());
        List<Reservation> reservations = Collections.singletonList(reservation);
        Page<Reservation> page = new PageImpl<>(reservations);

        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(user);
        when(reservationRepository.findByUserAndCriteriaAndDeletedAtIsNull(eq(user), isNull(), isNull(), isNull(), any(Pageable.class))).thenReturn(page);

        // When
        Page<ReservationSearchResponse> result = reservationService.search(authUser, 1, 10, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(reservation.getId(), result.getContent().get(0).getId());
    }

    // 예약 목록 조회 실패 테스트 케이스
    @Test
    void search_fail_userNotFound() {
        // Given
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(anyString())).thenThrow(new CustomException(ErrorCode.NOT_FOUND, User.class.getSimpleName()));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.search(new AuthUser(0L, "unknown", "unknown@example.com", UserRole.USER), 1, 10, null, null, null);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void search_fail_noReservationsFound() {
        // Given
        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());
        Page<Reservation> page = new PageImpl<>(Collections.emptyList());

        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(user);
        when(reservationRepository.findByUserAndCriteriaAndDeletedAtIsNull(eq(user), isNull(), isNull(), isNull(), any(Pageable.class))).thenReturn(page);

        // When
        Page<ReservationSearchResponse> result = reservationService.search(authUser, 1, 10, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    // 예약 수정 성공 테스트 케이스
    @Test
    void update_success() {
        // Given
        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(timeSlot2.getId(), LocalDate.of(2024, 11, 28));

        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(user);
        when(reservationRepository.findByIdAndUserOrThrow(reservation.getId(), user)).thenReturn(reservation);

        // 현재 예약 상태를 PENDING으로 설정
        reservation.updateStatus(ReservationStatus.PENDING);

        when(timeSlotRepository.findByIdOrThrow(updateRequest.getTimeId())).thenReturn(timeSlot2);

        // 새로운 시간 슬롯의 상태를 Mock
        when(reservationRepository.findByTimeSlotIdInAndStatusIn(anyList(), anyList())).thenReturn(Collections.emptyList());

        // 예약 업데이트 로직 Mock
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation savedReservation = invocation.getArgument(0);
            savedReservation.updateTime(timeSlot2, court);
            return savedReservation;
        });

        // When
        ReservationSearchDetailResponse response = reservationService.update(reservation.getId(), authUser, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals(reservation.getId(), response.getId());
        assertEquals(ReservationStatus.PENDING.name(), response.getStatus());
        assertEquals(LocalDate.of(2024, 11, 28), response.getDate());
    }

    // 예약 수정 실패 테스트 케이스
    @Test
    void update_fail_userNotFound() {
        // Given
        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.update(reservation.getId(), authUser, new ReservationUpdateRequest(timeSlot2.getId(), LocalDate.now()));
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void update_fail_reservationNotFound() {
        // Given
        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(user);
        when(reservationRepository.findByIdAndUserOrThrow(reservation.getId(), user)).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.update(reservation.getId(), authUser, new ReservationUpdateRequest(timeSlot2.getId(), LocalDate.now()));
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void update_fail_notAuthorized() {
        // Given
        User otherUser = new User("Other", "other@example.com", "인천", RANK_C, UserRole.USER);

        AuthUser otherUserAuth = new AuthUser(otherUser.getId(), otherUser.getName(), otherUser.getEmail(), otherUser.getUserRole());
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(otherUserAuth.getEmail())).thenReturn(otherUser);
        when(reservationRepository.findByIdAndUserOrThrow(reservation.getId(), otherUser)).thenThrow(new CustomException(ErrorCode.NO_AUTHORITY));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.update(reservation.getId(), otherUserAuth, new ReservationUpdateRequest(timeSlot2.getId(), LocalDate.now()));
        });
        assertEquals(ErrorCode.NO_AUTHORITY, exception.getErrorCode());
    }


    // 예약 삭제 성공 테스트 케이스
    @Test
    void delete_success() {
        // Given
        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(user);
        when(reservationRepository.findByIdAndUserOrThrow(reservation.getId(), user)).thenReturn(reservation);

        // 현재 예약 상태를 PENDING으로 설정
        reservation.updateStatus(ReservationStatus.PENDING);

        // When
        reservationService.delete(reservation.getId(), authUser);

        // Then
        assertEquals(ReservationStatus.CANCELED, reservation.getStatus());
    }


    // 예약 취소 실패 테스트 케이스
    @Test
    void delete_fail_userNotFound() {
        // Given: 사용자를 찾을 수 없도록 설정
        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // When & Then: 예외 발생 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.delete(reservation.getId(), authUser);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void delete_fail_reservationNotFound() {
        // Given
        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(user);
        when(reservationRepository.findByIdAndUserOrThrow(reservation.getId(), user)).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.delete(reservation.getId(), authUser);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }


    @Test
    void delete_fail_notAuthorized() {
        // Given
        User otherUser = new User("Other", "other@example.com", "인천", RANK_C, UserRole.USER);

        AuthUser otherUserAuth = new AuthUser(otherUser.getId(), otherUser.getName(), otherUser.getEmail(), otherUser.getUserRole());
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(otherUserAuth.getEmail())).thenReturn(otherUser);
        when(reservationRepository.findByIdAndUserOrThrow(reservation.getId(), otherUser)).thenThrow(new CustomException(ErrorCode.NO_AUTHORITY));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.delete(reservation.getId(), otherUserAuth);
        });
        assertEquals(ErrorCode.NO_AUTHORITY, exception.getErrorCode());
    }

    @Test
    void delete_fail_time_is_already_passed() {
        // Given
        reservation.updateStatus(ReservationStatus.ACCEPTED);

        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getEmail(), user.getUserRole());
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(user);
        when(reservationRepository.findByIdAndUserOrThrow(reservation.getId(), user)).thenReturn(reservation);

        // When & Then: 예외 발생 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.delete(reservation.getId(), authUser);
        });
        assertEquals(ErrorCode.TIME_IS_ALREAY_PASSED, exception.getErrorCode());
    }


    // 예약 상태 변경 성공 테스트 케이스
    @Test
    void change_fail_userNotFound() {
        // Given: 사용자를 찾을 수 없도록 설정
        AuthUser authUser = new AuthUser(admin.getId(), admin.getName(), admin.getEmail(), admin.getUserRole());
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest("ACCEPTED");

        // When & Then: 예외 발생 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.change(reservation.getId(), authUser, request);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    // 예약 상태 변경 실패 테스트 케이스
    @Test
    void change_fail_reservationNotFound() {
        // Given: 사용자 정보와 함께 예약 ID를 찾을 수 없도록 설정
        AuthUser authUser = new AuthUser(admin.getId(), admin.getName(), admin.getEmail(), admin.getUserRole());
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(admin);
        when(reservationRepository.findByIdAndCourt_Gym_User_OrThrow(reservation.getId(), admin)).thenThrow(new CustomException(ErrorCode.NOT_FOUND));

        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest("ACCEPTED");

        // When & Then: 예외 발생 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.change(reservation.getId(), authUser, request);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void change_fail_notAuthorized() {
        // Given
        User otherUser = new User("Other", "other@example.com", "인천", RANK_C, UserRole.USER);

        AuthUser otherUserAuth = new AuthUser(otherUser.getId(), otherUser.getName(), otherUser.getEmail(), otherUser.getUserRole());
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(otherUserAuth.getEmail())).thenReturn(otherUser);
        when(reservationRepository.findByIdAndCourt_Gym_User_OrThrow(reservation.getId(), otherUser)).thenThrow(new CustomException(ErrorCode.NO_AUTHORITY));

        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest("ACCEPTED");

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.change(reservation.getId(), otherUserAuth, request);
        });
        assertEquals(ErrorCode.NO_AUTHORITY, exception.getErrorCode());
    }

    @Test
    void change_fail_alreadyCanceled() {
        // Given: 예약 상태를 CANCELED로 설정
        reservation.updateStatus(ReservationStatus.CANCELED);
        AuthUser authUser = new AuthUser(admin.getId(), admin.getName(), admin.getEmail(), admin.getUserRole());
        when(userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail())).thenReturn(admin);
        when(reservationRepository.findByIdAndCourt_Gym_User_OrThrow(reservation.getId(), admin)).thenReturn(reservation);

        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest("ACCEPTED");

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.change(reservation.getId(), authUser, request);
        });
        assertEquals(ErrorCode.RESERVATION_CANT_ACCEPTED, exception.getErrorCode());
    }
}