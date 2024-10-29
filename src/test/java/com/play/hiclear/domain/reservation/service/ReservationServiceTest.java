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

        reservation = new Reservation(1L, user, timeSlot1, court, ReservationStatus.PENDING);

        // Mock 설정: 예약 객체를 찾을 수 있도록 설정
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
    }

    // 예약 생성 성공 테스트 케이스
    @Test
    void createReservations_success() {
        // Given
        ReservationRequest request = new ReservationRequest(Arrays.asList(timeSlot1.getId(), timeSlot2.getId()), court.getCourtNum());
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
        ReservationRequest request = new ReservationRequest(Arrays.asList(timeSlot1.getId(), timeSlot2.getId()), court.getId());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.create(user.getEmail(), request);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createReservations_fail_authorization() {
        // Given
        ReservationRequest request = new ReservationRequest(Arrays.asList(timeSlot1.getId()), court.getId());
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(courtRepository.findById(request.getCourtId())).thenReturn(Optional.of(court));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.create(admin.getEmail(), request);
        });
        assertEquals(ErrorCode.NO_AUTHORITY, exception.getErrorCode());
    }

    @Test
    void createReservations_fail_courtInactive() {
        // Given
        ReservationRequest request = new ReservationRequest(Arrays.asList(timeSlot1.getId()), court.getId());
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
        ReservationRequest request = new ReservationRequest(Arrays.asList(timeSlot1.getId(), timeSlot2.getId()), court.getId());
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
    void getAllReservations_success() {
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
    void getAllReservations_fail_userNotFound() {
        // Given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.search(user.getEmail());
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getAllReservations_fail_emptyList() {
        // Given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findByUserWithDetails(user)).thenReturn(Collections.emptyList());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.search(user.getEmail());
        });
        assertEquals(ErrorCode.RESERVATION_LIST_EMPTY, exception.getErrorCode());
    }

    // 예약 수정 성공 테스트 케이스
    @Test
    void updateReservation_success() {
        // Given
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(timeSlot2.getId());

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
            savedReservation.update(timeSlot2, court);
            return savedReservation;
        });

        // When
        ReservationSearchDetailResponse response = reservationService.update(reservation.getId(), user.getEmail(), updateRequest);

        // Then
        assertNotNull(response);
        assertEquals(reservation.getId(), response.getId());
        assertEquals(ReservationStatus.PENDING.name(), response.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class)); // save 호출 검증
    }

    // 예약 수정 실패 테스트 케이스
    @Test
    void updateReservation_fail_userNotFound() {
        // Given
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(timeSlot2.getId());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.update(reservation.getId(), user.getEmail(), updateRequest);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateReservation_fail_timeSlotAlreadyReserved() {
        // Given
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(timeSlot2.getId());
        Reservation existingReservation = new Reservation(2L, user, timeSlot2, court, ReservationStatus.PENDING);

        // Mock 설정
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
        when(timeSlotRepository.findById(updateRequest.getTimeId())).thenReturn(Optional.of(timeSlot2));
        when(reservationRepository.findByTimeSlotIdInAndStatusIn(anyList(), anyList())).thenReturn(Collections.singletonList(existingReservation));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.update(reservation.getId(), user.getEmail(), updateRequest);
        });

        // Then
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateReservation_fail_reservationNotFound() {
        // Given
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(timeSlot2.getId());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.update(reservation.getId(), user.getEmail(), updateRequest);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateReservation_fail_modificationNotAllowed() {
        // Given
        reservation.updateStatus(ReservationStatus.ACCEPTED);
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(timeSlot2.getId());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.update(reservation.getId(), user.getEmail(), updateRequest);
        });
        assertEquals(ErrorCode.NO_AUTHORITY, exception.getErrorCode());
    }

    // 예약 취소 성공 테스트 케이스
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

    // 예약 취소 실패 테스트 케이스
    @Test
    void deleteReservation_fail_userNotFound() {
        // Given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.delete(reservation.getId(), user.getEmail());
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void deleteReservation_fail_reservationNotFound() {
        // Given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.delete(reservation.getId(), user.getEmail());
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void deleteReservation_fail_alreadyCanceled() {
        // Given
        reservation.updateStatus(ReservationStatus.CANCELED);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.delete(reservation.getId(), user.getEmail());
        });
        assertEquals(ErrorCode.RESERVATION_CANT_CANCELED, exception.getErrorCode());
    }

    // 사장님 예약 수락/거절 성공 테스트 케이스
    @Test
    void changeReservation_success_accept() {
        // Given
        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest("ACCEPTED");
        reservation.updateStatus(ReservationStatus.PENDING);
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // When
        reservationService.change(reservation.getId(), admin.getEmail(), request);

        // Then
        assertEquals(ReservationStatus.ACCEPTED, reservation.getStatus());
        verify(reservationRepository, times(1)).save(reservation);
    }


    // 사장님 예약 수락/거절 실패 테스트 케이스
    @Test
    void changeReservation_fail_userNotFound() {
        // Given
        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest("ACCEPTED");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.change(reservation.getId(), user.getEmail(), request);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void changeReservation_fail_reservationNotFound() {
        // Given
        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest("ACCEPTED");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.change(reservation.getId(), user.getEmail(), request);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void changeReservation_fail_alreadyCanceled() {
        // Given
        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest("ACCEPTED");
        reservation.updateStatus(ReservationStatus.CANCELED);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.change(reservation.getId(), user.getEmail(), request);
        });
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void changeReservation_fail_invalidStatus() {
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
}