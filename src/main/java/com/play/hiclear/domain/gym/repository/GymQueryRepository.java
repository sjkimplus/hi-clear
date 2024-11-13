package com.play.hiclear.domain.gym.repository;

import com.play.hiclear.domain.gym.dto.response.GymSimpleResponse;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GymQueryRepository {

    Page<GymSimpleResponse> search(
            String name,
            String address,
            GymType gymType,
            Point userLocation,
            Double requestDistance,
            Pageable pageable);

    Page<GymSimpleResponse> searchv4(
            String name,
            String address,
            GymType gymType,
            Point userLocation,
            Double requestDistance,
            Pageable pageable);
}
