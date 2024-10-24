package com.play.hiclear.domain.reservation.repository;

import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRespository extends JpaRepository<Reservation, Long> {
    boolean existsByTimeSlotAndStatus(TimeSlot timeSlot, ReservationStatus status);
}
