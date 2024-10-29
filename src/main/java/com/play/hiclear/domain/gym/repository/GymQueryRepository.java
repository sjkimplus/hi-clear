package com.play.hiclear.domain.gym.repository;

import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GymQueryRepository {

    Page<Gym> searchGyms(
            String name,
            String address,
            GymType gymType,
            Pageable pageable);

}
