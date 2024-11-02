package com.play.hiclear.domain.reservation.repository;

import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationQueryRepository {
    List<Reservation> findByTimeSlotIdInAndStatusIn(List<Long> timeSlotIds, List<ReservationStatus> pending);

    List<Reservation> findByCourtAndDateAndTimeSlotIn(Court newCourt, LocalDate date, List<TimeSlot> newTimeSlot);

    Optional<Reservation> findByIdAndUser(Long reservationId, User user);

    Page<Reservation> findByGymUserAndDeletedAtIsNull(User user, Long courtId, ReservationStatus status,
                                    LocalDate date, Pageable pageable);

    Page<Reservation> findByUserAndCriteriaAndDeletedAtIsNull(User user, Long courtId, ReservationStatus status,
                                            LocalDate date, Pageable pageable);

    List<Reservation> findByTimeSlotIdInAndStatusInAndDate(List<Long> timeSlotIds, List<ReservationStatus> pending, LocalDate date);
}
