package com.play.hiclear.domain.schedule.repository;

import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.play.hiclear.domain.schduleparticipant.entity.QScheduleParticipant.scheduleParticipant;
import static com.play.hiclear.domain.schedule.entity.QSchedule.schedule;

@Repository
@RequiredArgsConstructor
public class ScheduleQueryRepositoryImpl implements ScheduleQueryRepository{

    private final JPAQueryFactory queryFactory;


    @Override
    public List<Schedule> findByClub(Club club) {
        return queryFactory.selectFrom(schedule)
                .join(schedule.club).fetchJoin()
                .join(schedule.scheduleParticipants, scheduleParticipant).fetchJoin()
                .join(scheduleParticipant.user).fetchJoin()
                .where(schedule.club.eq(club))
                .fetch();
    }
}
