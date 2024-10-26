package com.play.hiclear.domain.schduleparticipant.repository;

import com.play.hiclear.domain.schduleparticipant.entity.QScheduleParticipant;
import com.play.hiclear.domain.schduleparticipant.entity.ScheduleParticipant;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleParticipantQueryRepositoryImpl implements ScheduleParticipantQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByScheduleAndUser(Schedule savedSchedule, User participantUser) {
        QScheduleParticipant scheduleParticipant = QScheduleParticipant.scheduleParticipant;

        // 쿼리 작성
        Long count = queryFactory
                .select(scheduleParticipant.count())
                .from(scheduleParticipant)
                .where(scheduleParticipant.schedule.eq(savedSchedule)
                        .and(scheduleParticipant.user.eq(participantUser)))
                .fetchOne(); // 결과를 가져옴

        return count != null && count > 0; // 존재 여부를 반환
    }

    @Override
    public List<ScheduleParticipant> findBySchedule(Schedule schedule) {
        QScheduleParticipant scheduleParticipant = QScheduleParticipant.scheduleParticipant;

        return queryFactory
                .selectFrom(scheduleParticipant)
                .where(scheduleParticipant.schedule.eq(schedule))
                .fetch(); // 일정에 해당하는 모든 참가자를 가져옴
    }
}
