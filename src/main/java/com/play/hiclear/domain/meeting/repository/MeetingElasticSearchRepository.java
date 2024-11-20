package com.play.hiclear.domain.meeting.repository;

import com.play.hiclear.domain.meeting.entity.MeetingDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingElasticSearchRepository extends ElasticsearchRepository<MeetingDocument, Long> {

    // 제목으로 검색
    Page<MeetingDocument> findByTitleContaining(String title, Pageable pageable);

    // 지역주소로 검색
    Page<MeetingDocument> findByRegionAddressContaining(String regionAddress, Pageable pageable);

    // 급수로 필터링
    Page<MeetingDocument> findByRanks(String ranks, Pageable pageable);

    // 제목과 지역주소 둘 다 검색
    Page<MeetingDocument> findByTitleContainingOrRegionAddressContaining(String title, String regionAddress, Pageable pageable);
}