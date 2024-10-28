package com.play.hiclear.domain.reservation.repository;

import com.play.hiclear.domain.reservation.entity.QReservation;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationQueryRepositoryImpl implements ReservationQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Reservation> findByIdAndUserWithDetails(Long reservationId, User user) {
        QReservation reservation = QReservation.reservation;

        return Optional.ofNullable(queryFactory
                .selectFrom(reservation)
                .innerJoin(reservation.court).fetchJoin()
                .innerJoin(reservation.timeSlot).fetchJoin()
                .where(reservation.id.eq(reservationId)
                        .and(reservation.user.eq(user)))
                .fetchOne());
    }

    @Override
    public List<Reservation> findByUserWithDetails(User user) {
        QReservation reservation = QReservation.reservation;

        return queryFactory.selectFrom(reservation)
                .innerJoin(reservation.court)
                .fetchJoin()
                .innerJoin(reservation.timeSlot)
                .fetchJoin()
                .where(reservation.user.eq(user))
                .fetch();
    }

    @Override
    public List<Reservation> findByTimeSlotIdInAndStatusIn(List<Long> timeSlotIds, List<ReservationStatus> statuses) {
        QReservation reservation = QReservation.reservation;

        return queryFactory.selectFrom(reservation)
                .where(reservation.timeSlot.id.in(timeSlotIds)
                        .and(reservation.status.in(statuses)))
                .fetch();
    }
}