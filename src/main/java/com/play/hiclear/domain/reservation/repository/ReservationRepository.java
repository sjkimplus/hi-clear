package com.play.hiclear.domain.reservation.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationQueryRepository {

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.court c " +
            "JOIN FETCH r.timeSlot t " +
            "WHERE r.id = :reservationId AND r.user = :user")
    Optional<Reservation> findByIdAndUser(Long reservationId, User user);

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.court c " +
            "JOIN FETCH c.gym g " +
            "WHERE r.id = :reservationId AND g.user = :user")
    Optional<Reservation> findByIdAndCourt_Gym_User(Long reservationId, User user);

    default Reservation findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));
    }

    default Reservation findByIdAndCourt_Gym_User_OrThrow(Long reservationId, User user) {
        return findByIdAndCourt_Gym_User(reservationId, user).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));
    }

    default Reservation findByIdAndUserOrThrow(Long reservationId, User user) {
        return findByIdAndUser(reservationId, user).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));
    }
}