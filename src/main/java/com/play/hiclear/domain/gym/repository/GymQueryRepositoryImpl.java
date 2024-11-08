package com.play.hiclear.domain.gym.repository;

import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.play.hiclear.domain.gym.entity.QGym.gym;


@RequiredArgsConstructor
public class GymQueryRepositoryImpl implements GymQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Gym> searchGyms(String name,
                                String address,
                                GymType gymType,
                                Pageable pageable) {

        BooleanExpression condition = buildCondition(name, address, gymType);

        List<Gym> results = jpaQueryFactory
                .selectFrom(gym)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .selectFrom(gym)
                .where(condition)
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression buildCondition(String name, String address, GymType gymType) {
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

        return condition;
    }

}
