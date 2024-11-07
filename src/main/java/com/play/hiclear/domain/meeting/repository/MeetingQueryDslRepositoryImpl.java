package com.play.hiclear.domain.meeting.repository;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.meeting.dto.response.MeetingSearchResponse;
import com.play.hiclear.domain.meeting.enums.SortType;
import com.play.hiclear.domain.participant.enums.ParticipantStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.play.hiclear.domain.meeting.entity.QMeeting.meeting;
import static com.play.hiclear.domain.participant.entity.QParticipant.participant;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MeetingQueryDslRepositoryImpl implements MeetingQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MeetingSearchResponse> search(SortType sortType, Ranks ranks, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        // Subquery to count ACCEPTED participants for each meeting
        var joinedNumber = JPAExpressions
                .select(participant.count())
                .from(participant)
                .where(
                        participant.meeting.eq(meeting),
                        participant.status.eq(ParticipantStatus.ACCEPTED)
                );
        var query = queryFactory
                .select(
                        Projections.constructor(
                                MeetingSearchResponse.class,
                                meeting,
                                joinedNumber
                        )
                )
                .from(meeting)
                .where(
                        matchRank(ranks),
                        meeting.deletedAt.isNull(),
                        meeting.startTime.gt(now),
                        meeting.groupSize.gt(joinedNumber)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // Adjust sorting based on sortType
        if (sortType == SortType.EARLIEST) {
            query.orderBy(meeting.startTime.asc()); // Sort by earliest startTime
        } else {
            query.orderBy(meeting.id.desc()); // LATEST sort if not EARLIEST
        }

        List<MeetingSearchResponse> results = query.fetch();

        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(meeting)
                .where(
                        matchRank(ranks),
                        meeting.deletedAt.isNull(),
                        meeting.startTime.gt(now),
                        meeting.groupSize.gt(joinedNumber)
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount);
    }

    private BooleanExpression matchRank(Ranks ranks) {
        return ranks != null ? meeting.ranks.eq(ranks) : null;
    }
}
