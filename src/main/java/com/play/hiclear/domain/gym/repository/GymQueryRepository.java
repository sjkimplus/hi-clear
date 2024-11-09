package com.play.hiclear.domain.gym.repository;

import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GymQueryRepository {

    List<Gym> search(
            String name,
            String address,
            GymType gymType,
            Double userLatitude,
            Double userLongitude,
            Double requestDistance);
}
