package com.play.hiclear.domain.gym.repository;

import com.play.hiclear.domain.gym.dto.response.GymSimpleResponse;
import com.play.hiclear.domain.gym.enums.GymType;
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

import static com.play.hiclear.domain.gym.entity.QGym.gym;

@RequiredArgsConstructor
public class GymQueryRepositoryImpl implements GymQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<GymSimpleResponse> search(String name, String address, GymType gymType,
                              Point userLocation, Double requestDistance, Pageable pageable) {

        BooleanExpression condition = buildSearchCondition(
                name, address, gymType, userLocation, requestDistance);

        NumberTemplate<Double> distanceTemplate = Expressions.numberTemplate(
                Double.class,
                "ST_Distance_Sphere({0}, {1})", gym.location, userLocation
        );

        JPAQuery<Tuple> query = jpaQueryFactory
                .select(gym, distanceTemplate)
                .from(gym)
                .where(condition)
                .orderBy(distanceTemplate.asc());

        // 페이지로 변환
        List<GymSimpleResponse> gymResponses = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(tuple -> new GymSimpleResponse(
                        tuple.get(gym).getName(),
                        tuple.get(gym).getRegionAddress(),
                        Double.valueOf(String.format("%.1f", tuple.get(distanceTemplate) / 1000))
                ))
                .filter(gymSimpleResponse -> gymSimpleResponse.getDistance() <= requestDistance)
                .collect(Collectors.toList());

        long total = query.fetchCount();

        return new PageImpl<>(gymResponses, pageable, total);
    }

    private BooleanExpression buildSearchCondition(
            String name, String address, GymType gymType,
            Point userLocation, Double requestDistance) {

        BooleanExpression condition = gym.isNotNull();

        condition = condition.and(gym.deletedAt.isNull());
        if (requestDistance != null) {
//            Double distance = requestDistance / (111.32 * Math.cos(Math.toRadians(38.6)));
            requestDistance *= 1000;
            condition = condition.and(
                    Expressions.booleanTemplate(
                            "ST_CONTAINS(ST_Buffer({0}, {1}), {2})",
                            userLocation, requestDistance, gym.location
                    ));
        }

        if (name != null && !name.isEmpty()) {
            condition = condition.and(gym.name.containsIgnoreCase(name));
        }

        if (address != null && !address.isEmpty()) {
            condition = condition.and(gym.name.containsIgnoreCase(address));
        }

        if (gymType != null) {
            condition = condition.and(gym.gymType.eq(gymType));
        }

        return condition;
    }

}


