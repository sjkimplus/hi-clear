package com.play.hiclear.domain.schedule.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.schedule.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleQueryRepository {

    Optional<Schedule> findByIdAndDeletedAtIsNull(Long scheduleId);

    default Schedule findByIdAndDeletedAtIsNullOrThrow(Long scheduleId) {
        return findByIdAndDeletedAtIsNull(scheduleId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Schedule.class.getSimpleName()));
    }

//    @Query("SELECT s FROM Schedule s JOIN FETCH s.club WHERE s.club = :club AND s.deletedAt IS NULL " +
//            "AND (:title IS NULL OR s.title LIKE %:title%) " +
//            "AND (:description IS NULL OR s.description LIKE %:description%) " +
//            "AND (:region IS NULL OR s.region LIKE %:region%) " +
//            "AND (:startDate IS NULL OR s.startTime >= :startDate) " +
//            "AND (:endDate IS NULL OR s.endTime <= :endDate)")
//    Page<Schedule> findAllByClubAndDeletedAtIsNullAndFilters(Club club, String title, String description, String region, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}