package com.play.hiclear.domain.reservation.repository;

import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface ReservationQueryRepository {
    Optional<Reservation> findByIdAndUserWithDetails(Long reservationId, User user);

    List<Reservation> findByUserWithDetails(User user);

    List<Reservation> findByTimeSlotIdInAndStatusIn(List<Long> timeSlotIds, List<ReservationStatus> pending);
}
