package com.play.hiclear.domain.club.repository;

import com.play.hiclear.domain.club.entity.ClubDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ClubElasticsearchRepository extends ElasticsearchRepository<ClubDocument, String> {
    Page<ClubDocument> findByClubnameContainingAndIntroContainingAndRegionAddressContainingAndRoadAddressContaining(String clubname, String intro, String regionAddress, String roadAddress, Pageable pageable);

    Page<ClubDocument> findByIntroContaining(String intro, Pageable pageable);

    Page<ClubDocument> findByClubnameContaining(String clubname, Pageable pageable);

    Page<ClubDocument> findByRegionAddressContainingAndRoadAddressContaining(String regionAddress, String roadAddress, Pageable pageable);
}
