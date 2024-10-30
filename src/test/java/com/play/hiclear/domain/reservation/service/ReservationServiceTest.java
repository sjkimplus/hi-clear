package com.play.hiclear.domain.reservation.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.repository.CourtRepository;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        gym = new Gym("My Gym", "운동하기 좋은 체육관", "123 Main St", GymType.PUBLIC, admin);

        court = new Court(1L, 1L, 150000, true, gym);

        timeSlot1 = new TimeSlot(1L, LocalTime.of(10, 0), LocalTime.of(11, 0), court.getId(), court);
        timeSlot2 = new TimeSlot(2L, LocalTime.of(12, 0), LocalTime.of(13, 0), court.getId(), court);

        reservation = new Reservation(1L, user, timeSlot1, court, ReservationStatus.PENDING, LocalDate.of(2024, 11, 1));


        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
        when(timeSlotRepository.findById(timeSlot1.getId())).thenReturn(Optional.of(timeSlot1));
        when(timeSlotRepository.findById(timeSlot2.getId())).thenReturn(Optional.of(timeSlot2));
        when(courtRepository.findById(court.getId())).thenReturn(Optional.of(court));
    }

    // 예약 생성 성공 테스트 케이스
    @Test
    void createReservations_success() {
        // Given
        ReservationRequest request = new ReservationRequest(Arrays.asList(timeSlot1.getId(), timeSlot2.getId()), court.getCourtNum(), LocalDate.of(2024, 11, 3));
        setupMocksForCreate(request);

        // When
        List<ReservationSearchDetailResponse> result = reservationService.create(user.getEmail(), request);

        // Then
        assertEquals(2, result.size());
        verify(reservationRepository, times(1)).saveAll(anyList());
    }

    private void setupMocksForCreate(ReservationRequest request) {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(courtRepository.findById(request.getCourtId())).thenReturn(Optional.of(court));
        when(timeSlotRepository.findAllById(request.getTimeList())).thenReturn(Arrays.asList(timeSlot1, timeSlot2));
        when(reservationRepository.findByTimeSlotIdInAndStatusIn(anyList(), anyList())).thenReturn(Collections.emptyList());
    }

    // 예약 생성 실패 테스트 케이스
    @Test
    void createReservations_fail_userNotFound() {
        // Given
        ReservationRequest request = new ReservationRequest(Arrays.asList(timeSlot1.getId(), timeSlot2.getId()), court.getId(), LocalDate.of(2024, 11, 3));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.create(user.getEmail(), request);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }


    @Test
    void createReservations_fail_courtInactive() {
        // Given
        ReservationRequest request = new ReservationRequest(Arrays.asList(timeSlot1.getId()), court.getId(), LocalDate.of(2024, 11, 3));
        court = new Court(3L, 3L, 15000000, false, gym);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(courtRepository.findById(request.getCourtId())).thenReturn(Optional.of(court));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.create(user.getEmail(), request);
        });
        assertEquals(ErrorCode.NO_AUTHORITY, exception.getErrorCode());
    }

    @Test
    void createReservations_fail_timeSlotAlreadyReserved() {
        // Given
        ReservationRequest request = new ReservationRequest(Arrays.asList(timeSlot1.getId(), timeSlot2.getId()), court.getId(), LocalDate.of(2024, 11, 3));
        setupMocksForCreate(request);
        when(reservationRepository.findByTimeSlotIdInAndStatusIn(anyList(), anyList())).thenReturn(Collections.singletonList(reservation));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.create(user.getEmail(), request);
        });
        assertEquals(ErrorCode.TIME_SLOT_ALREADY_RESERVED, exception.getErrorCode());
    }

    // 예약 조회 성공 테스트 케이스
    @Test
    void getReservation_success() {
        // Given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // When
        ReservationSearchDetailResponse result = reservationService.get(reservation.getId(), user.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(reservation.getId(), result.getId());
    }

    // 예약 조회 실패 테스트 케이스
    @Test
    void getReservation_fail_userNotFound() {
        // Given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.get(reservation.getId(), user.getEmail());
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getReservation_fail_reservationNotFound() {
        // Given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.get(reservation.getId(), user.getEmail());
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    // 예약 목록 조회 성공 테스트 케이스
    @Test
    void searchReservations_success() {
        // Given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findByUserWithDetails(user)).thenReturn(Collections.singletonList(reservation));

        // When
        List<ReservationSearchResponse> result = reservationService.search(user.getEmail());

        // Then
        assertEquals(1, result.size());
    }

    // 예약 목록 조회 실패 테스트 케이스
    @Test
    void searchReservations_fail_userNotFound() {
        // Given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.search(user.getEmail());
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    // 예약 수정 성공 테스트 케이스
    @Test
    void updateTimeReservation_success() {
        // Given
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(timeSlot2.getId(), LocalDate.of(2024, 11, 4));

        // Mock 설정
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // 기존 예약 상태는 PENDING으로 설정
        reservation.updateStatus(ReservationStatus.PENDING);

        when(timeSlotRepository.findById(updateRequest.getTimeId())).thenReturn(Optional.of(timeSlot2));
        when(courtRepository.findById(timeSlot2.getCourt().getId())).thenReturn(Optional.of(court));

        // 새로운 시간 슬롯의 상태를 Mock
        when(reservationRepository.findByTimeSlotIdInAndStatusIn(anyList(), anyList())).thenReturn(Collections.emptyList());

        // 예약 업데이트 로직 Mock
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation savedReservation = invocation.getArgument(0);
            savedReservation.updateTime(timeSlot2, court);
            return savedReservation;
        });

        // When
        ReservationSearchDetailResponse response = reservationService.update(reservation.getId(), user.getEmail(), updateRequest);

        // Then
        assertNotNull(response);
        assertEquals(reservation.getId(), response.getId());
        assertEquals(ReservationStatus.PENDING.name(), response.getStatus());
        assertEquals(LocalDate.of(2024, 11, 4), response.getDate());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    // 예약 삭제 성공 테스트 케이스
    @Test
    void deleteReservation_success() {
        // Given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // When
        reservationService.delete(reservation.getId(), user.getEmail());

        // Then
        assertEquals(ReservationStatus.CANCELED, reservation.getStatus());
        verify(reservationRepository, times(1)).save(reservation);
    }

    // 예약 상태 변경 성공 테스트 케이스
    @Test
    void changeReservationStatus_success() {
        // Given
        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest(ReservationStatus.ACCEPTED.name());
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // When
        reservationService.change(reservation.getId(), admin.getEmail(), request);

        // Then
        assertEquals(ReservationStatus.ACCEPTED, reservation.getStatus()); // 상태가 변경되었는지 확인
        verify(reservationRepository, times(1)).save(reservation); // 예약 저장 확인
    }

    // 예약 상태 변경 실패 테스트 케이스
    @Test
    void changeReservationStatus_fail_userNotFound() {
        // Given
        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest(ReservationStatus.CANCELED.name()); // Use enum's name
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.change(reservation.getId(), user.getEmail(), request);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void changeReservationStatus_fail_reservationNotFound() {
        // Given
        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest(ReservationStatus.CANCELED.name()); // Use enum's name
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.change(reservation.getId(), user.getEmail(), request);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void changeReservationStatus_fail_notAuthorized() {
        // Given
        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest(ReservationStatus.CANCELED.name()); // Use enum's name
        User otherUser = new User("Other User", "other@example.com", "서울", RANK_C, UserRole.USER);
        when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherUser));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.change(reservation.getId(), otherUser.getEmail(), request);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void changeReservationStatus_fail_invalidStatus() {
        // Given
        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest("INVALID_STATUS");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.change(reservation.getId(), user.getEmail(), request);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void checkCancellationTimeLimit_success() {
        // Given
        LocalDateTime reservationDateTime = LocalDateTime.now().plusHours(25); // 25시간 후
        reservationService.checkCancellationTimeLimit(reservationDateTime); // 예외가 발생하지 않아야 함
    }

    @Test
    void checkCancellationTimeLimit_fail_tooLate() {
        // Given
        LocalDateTime reservationDateTime = LocalDateTime.now().plusHours(1); // 현재 시간 + 1시간
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.checkCancellationTimeLimit(reservationDateTime);
        });

        // Then
        assertEquals(ErrorCode.RESERVATION_CANT_CANCELED, exception.getErrorCode());
    }

    @Test
    void validateRequestDate_success() {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(1); // 내일
        reservationService.validateRequestDate(futureDate); // 예외가 발생하지 않아야 함
    }

    @Test
    void validateRequestDate_fail_pastDate() {
        // Given
        LocalDate pastDate = LocalDate.now().minusDays(1); // 어제
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.validateRequestDate(pastDate);
        });

        // Then
        assertEquals(ErrorCode.INVALID_DATE, exception.getErrorCode());
    }


    @Test
    void calculateReservationDateTime_success() {
        // Given
        TimeSlot timeSlot = new TimeSlot(1L, LocalTime.of(10, 0), LocalTime.of(11, 0), court.getId(), court);
        reservation = new Reservation(1L, user, timeSlot, court, ReservationStatus.PENDING, LocalDate.of(2024, 11, 3));

        // When
        LocalDateTime result = reservationService.calculateReservationDateTime(reservation);

        // Then
        LocalDateTime expected = LocalDateTime.of(2024, 11, 3, 10, 0);
        assertEquals(expected, result);
    }

    @Test
    void checkReservationAuthority_success() {
        // Given
        reservation = new Reservation(1L, user, timeSlot1, court, ReservationStatus.PENDING, LocalDate.of(2024, 11, 3));

        // When & Then (사용자와 예약자 동일)
        reservationService.checkReservationAuthority(reservation, user); // 예외가 발생하지 않아야 함
    }

    @Test
    void parseReservationStatus_success_accepted() {
        // When
        ReservationStatus result = reservationService.parseReservationStatus("ACCEPTED");

        // Then
        assertEquals(ReservationStatus.ACCEPTED, result);
    }
}