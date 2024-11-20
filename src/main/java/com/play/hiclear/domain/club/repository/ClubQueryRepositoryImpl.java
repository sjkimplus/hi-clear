package com.play.hiclear.domain.club.repository;

import com.play.hiclear.domain.club.dto.response.ClubNearResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import static com.play.hiclear.domain.club.entity.QClub.club;

@RequiredArgsConstructor
public class ClubQueryRepositoryImpl implements ClubQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ClubNearResponse> search(Point userLocation, Double requestDistance, Pageable pageable) {

        BooleanExpression condition = buildSearchCondition(userLocation, requestDistance);

        NumberTemplate<Double> distanceTemplate = Expressions.numberTemplate(
                Double.class,
                "ST_Distance_Sphere({0}, {1})",  club.location, userLocation
        );

        JPAQuery<Tuple> query = jpaQueryFactory
                .select(club, distanceTemplate)
                .from(club)
                .where(condition)
                .orderBy(distanceTemplate.asc());

        // 페이지로 변환
        List<ClubNearResponse> clubNearResponses = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(tuple -> new ClubNearResponse(
                        tuple.get(club).getClubname(),
                        tuple.get(club).getRegionAddress(),
                        Double.valueOf(String.format("%.1f", tuple.get(distanceTemplate) / 1000))
                ))
                .filter(ClubNearResponse -> ClubNearResponse.getDistance() <= requestDistance)
                .collect(Collectors.toList());

        long total = query.fetchCount();

        return new PageImpl<>(clubNearResponses, pageable, total);
    }

    private BooleanExpression buildSearchCondition(
            Point userLocation, Double requestDistance) {

        BooleanExpression condition = club.isNotNull();

        condition = condition.and(club.deletedAt.isNull());

        if (requestDistance != null) {
            requestDistance *= 1000;
            condition = condition.and(
                    Expressions.booleanTemplate(
                            "ST_CONTAINS(ST_Buffer({0}, {1}), {2})",
                            userLocation, requestDistance, club.location
                    ));
        }

        return condition;
    }
}
