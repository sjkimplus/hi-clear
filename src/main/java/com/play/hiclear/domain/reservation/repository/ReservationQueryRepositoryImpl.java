package com.play.hiclear.domain.reservation.repository;

import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.reservation.entity.QReservation;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
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

    @Override
    public List<Reservation> findByCourtAndDateAndTimeSlotIn(Court newCourt, LocalDate date, List<TimeSlot> newTimeSlot) {
        QReservation reservation = QReservation.reservation;

        return queryFactory.selectFrom(reservation)
                .innerJoin(reservation.court) // 코트와 조인
                .innerJoin(reservation.timeSlot) // 시간 슬롯과 조인
                .where(reservation.court.eq(newCourt) // 특정 코트와 일치하는 예약
                        .and(reservation.date.eq(date)) // 특정 날짜와 일치하는 예약
                        .and(reservation.timeSlot.in(newTimeSlot))) // 주어진 시간 슬롯 목록 중 하나와 일치하는 예약
                .fetch();
    }
}