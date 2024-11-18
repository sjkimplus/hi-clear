package com.play.hiclear.domain.schedule.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.schedule.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleQueryRepository {

    Optional<Schedule> findByIdAndDeletedAtIsNull(Long scheduleId);

    default Schedule findByIdAndDeletedAtIsNullOrThrow(Long scheduleId) {
        return findByIdAndDeletedAtIsNull(scheduleId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Schedule.class.getSimpleName()));
    }

    @Query("SELECT s FROM Schedule s JOIN FETCH s.club WHERE s.club = :club AND s.deletedAt IS NULL " +
            "AND (:title IS NULL OR s.title LIKE %:title%) " +
            "AND (:description IS NULL OR s.description LIKE %:description%) " +
            "AND (:regionAddress IS NULL OR s.regionAddress LIKE %:regionAddress%) " +
            "AND (:startDate IS NULL OR s.startTime >= :startDate) " +
            "AND (:endDate IS NULL OR s.endTime <= :endDate)")
    Page<Schedule> findAllByClubAndDeletedAtIsNullAndFilters(Club club, String title, String description, String regionAddress, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT s.club FROM Schedule s " +
            "WHERE s.deletedAt IS NULL " +
            "AND s.startTime BETWEEN :dayStart AND :dayEnd  " +
            "AND s.regionAddress = :regionAddress " +
            "GROUP BY s.club " +
            "HAVING COUNT(s.id) >= 4")
    List<Club> findAllClubsByScheduleAtGym(@Param("dayStart") LocalDateTime dayStart,
                                           @Param("dayEnd") LocalDateTime dayEnd,
                                           @Param("regionAddress") String regionAddress);

    @Query("SELECT s FROM Schedule s WHERE s.deletedAt IS NULL " +
            "AND s.startTime BETWEEN :dayStart AND :dayEnd " +
            "AND s.regionAddress = :regionAddress ")
    List<Schedule> findSchedulesByDayAndLocation(@Param("dayStart") LocalDateTime dayStart,
                                                 @Param("dayEnd") LocalDateTime dayEnd,
                                                 @Param("regionAddress") String regionAddress);

    // 만료된 일정들을 현재 시간 기준으로 찾는 쿼리
    List<Schedule> findByEndTimeBefore(LocalDateTime now);

    // 만료된 일정 삭제 메서드
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.endTime < :now")
    void deleteExpiredSchedules(@Param("now") LocalDateTime now);

}