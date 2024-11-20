package com.play.hiclear.domain.club.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.club.entity.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long>, ClubQueryRepository {

    Optional<Club> findByIdAndDeletedAtIsNull(Long clubId);

    default Club findByIdAndDeletedAtIsNullOrThrow(Long clubId){
        return findByIdAndDeletedAtIsNull(clubId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Club.class.getSimpleName()));
    }

    Page<Club> findAll(Pageable pageable);

    @Query("SELECT c FROM Club c WHERE c.deletedAt IS NULL " +
        "AND (:clubname IS NULL OR c.clubname LIKE %:clubname%) " +
        "AND (:intro IS NULL OR c.intro LIKE %:intro%)" +
        "AND (:regionAddress IS NULL OR c.regionAddress LIKE %:regionAddress%)" +
        "AND (:roadAddress IS NULL OR c.roadAddress LIKE %:roadAddress%)"
    )
    Page<Club> findByDeletedAtIsNullAndFilters(String clubname, String intro, String regionAddress, String roadAddress, Pageable pageable);

    /*
        @Query("SELECT s FROM Schedule s JOIN FETCH s.club WHERE s.club = :club AND s.deletedAt IS NULL " +
            "AND (:title IS NULL OR s.title LIKE %:title%) " +
            "AND (:description IS NULL OR s.description LIKE %:description%) " +
            "AND (:regionAddress IS NULL OR s.regionAddress LIKE %:regionAddress%) " +
            "AND (:startDate IS NULL OR s.startTime >= :startDate) " +
            "AND (:endDate IS NULL OR s.endTime <= :endDate)")
    Page<Schedule> findAllByClubAndDeletedAtIsNullAndFilters(Club club, String title, String description, String regionAddress, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

     */
}
