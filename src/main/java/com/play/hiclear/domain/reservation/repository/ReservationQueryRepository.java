package com.play.hiclear.domain.reservation.repository;

import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationQueryRepository {
    List<Reservation> findByUserWithDetails(User user);

    List<Reservation> findByTimeSlotIdInAndStatusIn(List<Long> timeSlotIds, List<ReservationStatus> pending);

    List<Reservation> findByCourtAndDateAndTimeSlotIn(Court newCourt, LocalDate date, List<TimeSlot> newTimeSlot);
}
