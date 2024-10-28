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
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CourtRepository courtRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;

    // 예약 생성
    @Transactional
    public List<ReservationSearchDetailResponse> create(String email, ReservationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "User 객체를"));

        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "Court 객체를"));

        // 모든 타임슬롯을 한 번에 조회
        List<TimeSlot> timeSlots = timeSlotRepository.findAllById(request.getTimeList());

        // 타임슬롯 IDs로 예약된 상태를 한 번에 확인
        List<Long> timeSlotIds = timeSlots.stream().map(TimeSlot::getId).toList();
        List<Reservation> existingReservations = reservationRepository.findByTimeSlotIdInAndStatusIn(
                timeSlotIds, List.of(ReservationStatus.PENDING, ReservationStatus.ACCEPTED));

        // 중복 예약 상태 체크
        if (!existingReservations.isEmpty()) {
            throw new CustomException(ErrorCode.TIME_SLOT_ALREADY_RESERVED);
        }

        // 예약 객체 생성
        List<Reservation> reservations = timeSlots.stream()
                .map(timeSlot -> new Reservation(user, court, timeSlot, ReservationStatus.PENDING))
                .toList();

        reservationRepository.saveAll(reservations);
        return reservations.stream().map(ReservationSearchDetailResponse::from).toList();
    }


    // 예약 조회(단건)
    public ReservationSearchDetailResponse get(Long reservationId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "User 객체를"));

        Reservation reservation = reservationRepository.findByIdAndUserWithDetails(reservationId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "Reservation 객체를"));

        return ReservationSearchDetailResponse.from(reservation);
    }

    // 예약 목록 조회(다건)
    @Transactional
    public List<ReservationSearchResponse> search(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "User 객체를"));

        List<Reservation> reservations = reservationRepository.findByUserWithDetails(user);

        if (reservations.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_LIST_EMPTY);
        }

        return reservations.stream()
                .map(ReservationSearchResponse::from)
                .toList();
    }


    // 예약 수정
    @Transactional
    public ReservationSearchDetailResponse update(Long reservationId, String email, ReservationUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "User 객체를"));

        Reservation reservation = reservationRepository.findById(reservationId)
                .filter(res -> res.getUser().equals(user))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "Reservation 객체를"));

        // 현재 예약 상태 체크
        if (reservation.getStatus() == ReservationStatus.ACCEPTED ||
                reservation.getStatus() == ReservationStatus.REJECTED ||
                reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, "수락, 거절, 취소된 예약은 수정에");
        }

        // 새로운 시간 슬롯 확인
        TimeSlot newTimeSlot = timeSlotRepository.findById(request.getTimeId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "TimeSlot 객체를"));

        // 새로운 시간 슬롯의 상태를 한 번에 확인
        List<ReservationStatus> statuses = List.of(ReservationStatus.PENDING, ReservationStatus.ACCEPTED);
        boolean isTimeSlotReserved = reservationRepository.findByTimeSlotIdInAndStatusIn(
                List.of(newTimeSlot.getId()), statuses).size() > 0;

        if (isTimeSlotReserved) {
            throw new CustomException(ErrorCode.TIME_SLOT_ALREADY_RESERVED);
        }

        // 새로운 코트 자동 연결
        Court newCourt = courtRepository.findById(newTimeSlot.getCourt().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "Court 객체를"));

        // 예약 수정
        reservation.update(newTimeSlot, newCourt);
        Reservation updatedReservation = reservationRepository.save(reservation);

        return ReservationSearchDetailResponse.from(updatedReservation);
    }

    // 예약 취소
    @Transactional
    public void delete(Long reservationId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "User 객체를"));

        Reservation reservation = reservationRepository.findById(reservationId)
                .filter(res -> res.getUser().equals(user))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "Reservation 객체를"));

        // 예약 상태를 CANCELED로 변경
        reservation.updateStatus(ReservationStatus.CANCELED);

        // 예약 삭제 메서드 호출
        reservation.markDeleted();

        // 예약 업데이트
        reservationRepository.save(reservation);
    }
}