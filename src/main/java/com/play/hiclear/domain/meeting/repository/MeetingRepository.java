package com.play.hiclear.domain.meeting.repository;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.meeting.entity.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Optional<Meeting> findByIdAndDeletedAtIsNull(Long id);

    default Meeting findByIdAndDeletedAtIsNullOrThrow(Long id){
        return findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Meeting.class.getSimpleName()));
    }

    @Query("SELECT m FROM Meeting m WHERE m.ranks = :ranks AND m.deletedAt IS NULL")
    Page<Meeting> findByRanks(@Param("ranks") Ranks ranks, Pageable pageable);

    @Query("SELECT m FROM Meeting m WHERE (m.title LIKE %:title% OR m.regionAddress LIKE %:regionAddress%) AND m.deletedAt IS NULL")
    Page<Meeting> findByTitleContainingOrRegionAddressContaining(@Param("title") String title, @Param("regionAddress") String regionAddress, Pageable pageable);
}
