package com.play.hiclear.domain.reservation.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.repository.CourtRepository;
import com.play.hiclear.domain.reservation.dto.request.ReservationRequest;
import com.play.hiclear.domain.reservation.dto.response.ReservationResponse;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.reservation.repository.ReservationRespository;
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

    private final ReservationRespository reservationRepository;
    private final CourtRepository courtRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;

    // 예약 생성
    @Transactional
    public List<ReservationResponse> createReservations(String email, ReservationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        List<Reservation> reservations = request.getTimeList().stream()
                .map(timeSlotId -> {

                    TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                    // 시간 슬롯이 예약된 경우 체크
                    if (reservationRepository.existsByTimeSlotAndStatus(timeSlot, ReservationStatus.PENDING) ||
                            reservationRepository.existsByTimeSlotAndStatus(timeSlot, ReservationStatus.ACCEPTED)) {
                        throw new CustomException(ErrorCode.TIME_SLOT_ALREADY_RESERVED);
                    }
                    // 코트 찾기
                    Court court = courtRepository.findById(request.getCourtId())
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                    return new Reservation(user, court, timeSlot, ReservationStatus.PENDING);
                })
                .toList();

        reservationRepository.saveAll(reservations);
        return reservations.stream().map(ReservationResponse::from).toList();
    }


    // 예약 조회(단건)
    public ReservationResponse getReservation(Long reservationId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findByIdAndUser(reservationId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        return ReservationResponse.from(reservation);
    }

    // 예약 목록 조회(다건)
    public List<ReservationResponse> getAllReservations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_USER_NOT_FOUND));

        List<Reservation> reservations = reservationRepository.findByUser(user);

        if (reservations.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_LIST_EMPTY);
        }
        return reservations.stream().map(ReservationResponse::from).toList();
    }


}