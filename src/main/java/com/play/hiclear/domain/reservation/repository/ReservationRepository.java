package com.play.hiclear.domain.reservation.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
    Optional<Reservation> findByIdAndCourtGymUser(Long reservationId, User user);

    Optional<Reservation> findByIdAndDeletedAtIsNull(Long reservationId);

    // 만료된 예약 삭제 메서드
    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.date < :nowDate OR (r.date = :nowDate AND r.timeSlot.endTime < :nowTime)")
    void deleteExpiredReservations(@Param("nowDate") LocalDate nowDate, @Param("nowTime") LocalTime nowTime);


    default Reservation findByIdAndDeletedAtIsNullOrThrow(Long id) {
        return findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));
    }

    default Reservation findByIdAndCourtGymUserOrThrow(Long reservationId, User user) {
        return findByIdAndCourtGymUser(reservationId, user).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));
    }

    default Reservation findByIdAndUserOrThrow(Long reservationId, User user) {
        return findByIdAndUser(reservationId, user).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));
    }


}