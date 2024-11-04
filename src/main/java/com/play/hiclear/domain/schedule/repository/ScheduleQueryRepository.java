package com.play.hiclear.domain.schedule.repository;


import java.time.LocalDateTime;

public interface ScheduleQueryRepository {
    boolean existsByClubIdAndStartTimeAndTitleAndDeletedAtIsNull(Long clubId, LocalDateTime startTime, String title);
}