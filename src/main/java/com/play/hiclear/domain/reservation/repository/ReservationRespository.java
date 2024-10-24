package com.play.hiclear.domain.reservation.repository;

import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRespository extends JpaRepository<Reservation, Long> {
    boolean existsByTimeSlotAndStatus(TimeSlot timeSlot, ReservationStatus status);

    Optional<Reservation> findByIdAndUser(Long reservationId, User user);
}
