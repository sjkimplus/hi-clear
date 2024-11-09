package com.play.hiclear.domain.gym.repository;

import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.play.hiclear.domain.gym.entity.QGym.gym;


@RequiredArgsConstructor
public class GymQueryRepositoryImpl implements GymQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Gym> search(
            String name, String address, GymType gymType,
            Double userLatitude, Double userLongitude, Double requestDistance) {

        BooleanExpression condition = buildSearchCondition(name, address, gymType, userLatitude, userLongitude, requestDistance);

        return jpaQueryFactory
                .selectFrom(gym)
                .where(condition)
                .fetch();
    }


    private BooleanExpression buildSearchCondition(
            String name, String address, GymType gymType,
            Double userLatitude, Double userLongitude, Double requestDistance) {

        BooleanExpression condition = gym.isNotNull();

        condition = condition.and(gym.deletedAt.isNull());

        if (name != null && !name.isEmpty()) {
            condition = condition.and(gym.name.containsIgnoreCase(name));
        }

        if (address != null && !address.isEmpty()) {
            condition = condition.and(gym.name.containsIgnoreCase(address));
        }

        if (gymType != null) {
            condition = condition.and(gym.gymType.eq(gymType));
        }

        if (requestDistance != null){
            // 두 지점 간의 거리를 계산하는 표현식 생성
            NumberExpression<Double> distanceExpression = Expressions.numberTemplate(
                    Double.class,
                    "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1})))",
                    userLatitude, gym.latitude, gym.longitude, userLongitude
            );

            condition = condition.and(distanceExpression.loe(requestDistance));
        }

        return condition;
    }
}
