package com.play.hiclear.domain.reservation.repository;

import com.play.hiclear.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationQueryRepository {
}
