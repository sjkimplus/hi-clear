package com.play.hiclear.domain.reservation.repository;

import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.entity.QCourt;
import com.play.hiclear.domain.gym.entity.QGym;
import com.play.hiclear.domain.reservation.entity.QReservation;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationQueryRepositoryImpl implements ReservationQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Reservation> findByUserWithDetails(User user) {
        QReservation reservation = QReservation.reservation;

        return queryFactory.selectFrom(reservation)
                .innerJoin(reservation.court).fetchJoin()
                .innerJoin(reservation.timeSlot).fetchJoin()
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

    @Override
    public List<Reservation> findByCourtAndDateAndTimeSlotIn(Court newCourt, LocalDate date, List<TimeSlot> newTimeSlot) {
        QReservation reservation = QReservation.reservation;

        return queryFactory.selectFrom(reservation)
                .innerJoin(reservation.court).fetchJoin()
                .innerJoin(reservation.timeSlot).fetchJoin()
                .where(reservation.court.eq(newCourt)
                        .and(reservation.date.eq(date))
                        .and(reservation.timeSlot.in(newTimeSlot)))
                .fetch();
    }

    @Override
    public Optional<Reservation> findByIdAndUser(Long reservationId, User user) {
        QReservation reservation = QReservation.reservation;

        return Optional.ofNullable(queryFactory.selectFrom(reservation)
                .innerJoin(reservation.court).fetchJoin()
                .innerJoin(reservation.timeSlot).fetchJoin()
                .where(reservation.id.eq(reservationId)
                        .and(reservation.user.eq(user)))
                .fetchOne());
    }

    @Override
    public Page<Reservation> findByGymUser(User user, Long courtId, ReservationStatus status,
                                           LocalDate date,
                                           Pageable pageable) {
        QReservation reservation = QReservation.reservation;
        QCourt court = QCourt.court;
        QGym gym = QGym.gym;

        BooleanExpression predicate = gym.user.eq(user);

        if (courtId != null) {
            predicate = predicate.and(reservation.court.id.eq(courtId));
        }
        if (status != null) {
            predicate = predicate.and(reservation.status.eq(status));
        }
        if (date != null) {
            predicate = predicate.and(reservation.date.eq(date));
        }

        List<Reservation> reservations = queryFactory
                .selectFrom(reservation)
                .join(reservation.court, court).fetchJoin()
                .join(court.gym, gym).fetchJoin()
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(reservation)
                .join(reservation.court, court)
                .join(court.gym, gym)
                .where(predicate)
                .fetchCount();

        return new PageImpl<>(reservations, pageable, total);
    }

    @Override
    public Page<Reservation> findByUserAndCriteria(User user, Long courtId, ReservationStatus status,
                                                   LocalDate date,
                                                   Pageable pageable) {
        QReservation reservation = QReservation.reservation;
        QCourt court = QCourt.court;

        BooleanExpression predicate = reservation.user.eq(user);

        if (courtId != null) {
            predicate = predicate.and(reservation.court.id.eq(courtId));
        }
        if (status != null) {
            predicate = predicate.and(reservation.status.eq(status));
        }
        if (date != null) {
            predicate = predicate.and(reservation.date.eq(date));
        }

        List<Reservation> reservations = queryFactory
                .selectFrom(reservation)
                .join(reservation.court, court).fetchJoin()
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(reservation)
                .where(predicate)
                .fetchCount();

        return new PageImpl<>(reservations, pageable, total);
    }

    @Override
    public List<Reservation> findByTimeSlotIdInAndStatusInAndDate(List<Long> timeSlotIds, List<ReservationStatus> statuses, LocalDate date) {
        QReservation reservation = QReservation.reservation;

        return queryFactory.selectFrom(reservation)
                .where(reservation.timeSlot.id.in(timeSlotIds)
                        .and(reservation.status.in(statuses))
                        .and(reservation.date.eq(date)))
                .fetch();
    }
}