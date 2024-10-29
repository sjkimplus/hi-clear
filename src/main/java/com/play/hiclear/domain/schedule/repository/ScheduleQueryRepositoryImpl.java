package com.play.hiclear.domain.schedule.repository;

import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.user.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.play.hiclear.domain.schduleparticipant.entity.QScheduleParticipant.scheduleParticipant;
import static com.play.hiclear.domain.schedule.entity.QSchedule.schedule;

@Repository
@RequiredArgsConstructor
public class ScheduleQueryRepositoryImpl implements ScheduleQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByClubIdAndStartTimeAndTitleAndDeletedAtIsNull(Long clubId, LocalDateTime startTime, String title) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(schedule.club.id.eq(clubId));

        // startTime이 null이 아닐 때만 조건 추가
        if (startTime != null) {
            builder.and(schedule.startTime.eq(startTime));
        } else {
            builder.and(schedule.startTime.isNull());
        }

        // title이 null이 아닐 때만 조건 추가
        if (title != null) {
            builder.and(schedule.title.eq(title));
        } else {
            builder.and(schedule.title.isNull());
        }

        builder.and(schedule.deletedAt.isNull());

        return queryFactory.selectOne()
                .from(schedule)
                .where(builder)
                .fetchFirst() != null;
    }

    @Override
    public List<Schedule> findByClubAndDeletedAtIsNull(Club club) {
        return queryFactory.selectFrom(schedule)
                .join(schedule.user) // Fetch associated user
                .where(schedule.club.eq(club)
                        .and(schedule.deletedAt.isNull()))
                .fetch();
    }
}
