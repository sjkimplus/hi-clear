package com.play.hiclear.domain.gym.repository;

import com.play.hiclear.domain.gym.dto.response.GymSimpleResponse;
import com.play.hiclear.domain.gym.enums.GymType;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
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
    public Page<GymSimpleResponse> search(
            String name, String address, GymType gymType,
            Point userLocation, Double requestDistance, Pageable pageable) {

        BooleanExpression condition = buildSearchCondition(name, address, gymType, userLocation, requestDistance);

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
            condition = condition.and(
                    Expressions.numberTemplate(Double.class,
                                    "ST_Distance_Sphere({0}, {1})",
                                    gym.location, userLocation)
                            .loe(requestDistance)
            );
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


    @Override
    public Page<GymSimpleResponse> searchv4(String name, String address, GymType gymType,
                              Point userLocation, Double requestDistance, Pageable pageable) {

        BooleanExpression condition = buildSearchConditionV3(
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
                .collect(Collectors.toList());

        long total = query.fetchCount();

        return new PageImpl<>(gymResponses, pageable, total);
    }

    private BooleanExpression buildSearchConditionV3(
            String name, String address, GymType gymType,
            Point userLocation, Double requestDistance) {

        BooleanExpression condition = gym.isNotNull();

        condition = condition.and(gym.deletedAt.isNull());
        if (requestDistance != null) {

            condition = condition.and(
                    Expressions.numberTemplate(Double.class,
                                    "ST_Distance_Sphere({0}, {1})",
                                    gym.location, userLocation)
                            .loe(requestDistance)
            );
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

    // 공간 연산을 사용할 수 있는 helper class 예시
    public class SpatialOperations {
        public static BooleanExpression distance(Expression<Point> point1, Expression<Point> point2) {
            return Expressions.booleanTemplate("ST_Distance({0}, {1})", point1, point2);
        }
    }
}


