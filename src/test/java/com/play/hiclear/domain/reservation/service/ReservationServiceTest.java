package com.play.hiclear.domain.reservation.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.repository.CourtRepository;
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

    private User user;
    private Court court;
    private TimeSlot timeSlot1;
    private TimeSlot timeSlot2;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(1L, "John Doe", "john@example.com", "전라북도 익산시", null, UserRole.BUSINESS);
        court = new Court(1L, 1L, 10000);
        timeSlot1 = new TimeSlot(1L, LocalTime.of(10, 0), LocalTime.of(11, 0), court);
        timeSlot2 = new TimeSlot(2L, LocalTime.of(12, 0), LocalTime.of(13, 0), court);
        reservation = new Reservation(user, court, timeSlot1, ReservationStatus.PENDING);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
    }

    // 예약 생성 성공 테스트 케이스
    @Test
    void createReservations_success() {
        // given
        ReservationRequest request = new ReservationRequest(Arrays.asList(timeSlot1.getId(), timeSlot2.getId()), court.getId());
        setupMocksForCreate(request);

        // when
        List<ReservationSearchDetailResponse> result = reservationService.create(user.getEmail(), request);

        // then
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
        // given
        ReservationRequest request = new ReservationRequest(Arrays.asList(timeSlot1.getId(), timeSlot2.getId()), court.getId());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.create(user.getEmail(), request);
        });

        // then
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createReservations_fail_timeSlotAlreadyReserved() {
        // given
        ReservationRequest request = new ReservationRequest(Arrays.asList(timeSlot1.getId(), timeSlot2.getId()), court.getId());
        setupMocksForCreate(request);
        when(reservationRepository.findByTimeSlotIdInAndStatusIn(anyList(), anyList())).thenReturn(Collections.singletonList(reservation));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.create(user.getEmail(), request);
        });

        // then
        assertEquals(ErrorCode.TIME_SLOT_ALREADY_RESERVED, exception.getErrorCode());
    }

    // 예약 조회 성공 테스트 케이스
    @Test
    void getReservation_success() {
        // given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findByIdAndUserWithDetails(reservation.getId(), user)).thenReturn(Optional.of(reservation));

        // when
        ReservationSearchDetailResponse result = reservationService.get(reservation.getId(), user.getEmail());

        // then
        assertNotNull(result);
        assertEquals(reservation.getId(), result.getId());
    }

    // 예약 조회 실패 테스트 케이스
    @Test
    void getReservation_fail_userNotFound() {
        // given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.get(reservation.getId(), user.getEmail());
        });

        // then
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getReservation_fail_reservationNotFound() {
        // given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findByIdAndUserWithDetails(reservation.getId(), user)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.get(reservation.getId(), user.getEmail());
        });

        // then
        assertEquals(ErrorCode.RESERVATION_NOT_FOUND, exception.getErrorCode());
    }

    // 예약 목록 조회 성공 테스트 케이스
    @Test
    void getAllReservations_success() {
        // given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findByUserWithDetails(user)).thenReturn(Collections.singletonList(reservation));

        // when
        List<ReservationSearchResponse> result = reservationService.search(user.getEmail());

        // then
        assertEquals(1, result.size());
    }

    // 예약 목록 조회 실패 테스트 케이스
    @Test
    void getAllReservations_fail_userNotFound() {
        // given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.search(user.getEmail());
        });

        // then
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getAllReservations_fail_emptyList() {
        // given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findByUserWithDetails(user)).thenReturn(Collections.emptyList());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.search(user.getEmail());
        });

        // then
        assertEquals(ErrorCode.RESERVATION_LIST_EMPTY, exception.getErrorCode());
    }

    // 예약 수정 성공 테스트 케이스
    @Test
    void updateReservation_success() {
        // given
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(timeSlot2.getId());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
        when(timeSlotRepository.findById(updateRequest.getTimeId())).thenReturn(Optional.of(timeSlot2));
        when(reservationRepository.findByTimeSlotIdInAndStatusIn(anyList(), anyList())).thenReturn(Collections.emptyList());
        when(courtRepository.findById(timeSlot2.getCourt().getId())).thenReturn(Optional.of(court));

        // when
        ReservationSearchDetailResponse response = reservationService.update(reservation.getId(), user.getEmail(), updateRequest);

        // then
        assertNotNull(response);
        assertEquals(ReservationStatus.PENDING.name(), response.getStatus());
        verify(reservationRepository, times(1)).save(reservation);
    }

    // 예약 수정 실패 테스트 케이스
    @Test
    void updateReservation_fail_userNotFound() {
        // given
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(timeSlot2.getId());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.update(reservation.getId(), user.getEmail(), updateRequest);
        });

        // then
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateReservation_fail_reservationNotFound() {
        // given
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(timeSlot2.getId());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.update(reservation.getId(), user.getEmail(), updateRequest);
        });

        // then
        assertEquals(ErrorCode.RESERVATION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateReservation_fail_modificationNotAllowed() {
        // given
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(timeSlot2.getId());
        reservation = new Reservation(user, court, timeSlot1, ReservationStatus.ACCEPTED);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.update(reservation.getId(), user.getEmail(), updateRequest);
        });

        // then
        assertEquals(ErrorCode.RESERVATION_MODIFICATION_NOT_ALLOWED, exception.getErrorCode());
    }

    // 예약 취소 성공 테스트 케이스
    @Test
    void deleteReservation_success() {
        // given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        // when
        reservationService.delete(reservation.getId(), user.getEmail());

        // then
        // 상태가 CANCELED로 변경되었는지 확인
        assertEquals(ReservationStatus.CANCELED, reservation.getStatus());

        // 예약이 삭제되었다고 기대
        verify(reservationRepository, times(1)).save(reservation); // 업데이트 메서드 확인
    }

    // 예약 취소 실패 테스트 케이스
    @Test
    void deleteReservation_fail_userNotFound() {
        // given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.delete(reservation.getId(), user.getEmail());
        });

        // then
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void deleteReservation_fail_reservationNotFound() {
        // given
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.delete(reservation.getId(), user.getEmail());
        });

        // then
        assertEquals(ErrorCode.RESERVATION_NOT_FOUND, exception.getErrorCode());
    }
}