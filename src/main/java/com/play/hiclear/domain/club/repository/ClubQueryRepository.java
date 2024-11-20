package com.play.hiclear.domain.club.repository;

import com.play.hiclear.domain.club.dto.response.ClubNearResponse;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClubQueryRepository {
    Page<ClubNearResponse> search(
            Point userLocation,
            Double requestDistance,
            Pageable pageable);
}
