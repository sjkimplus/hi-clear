package com.play.hiclear.domain.schedule.repository;


import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleQueryRepository {
    boolean existsByClubIdAndStartTimeAndTitleAndDeletedAtIsNull(Long clubId, LocalDateTime startTime, String title);

    List<Schedule> findByClubAndDeletedAtIsNull(Club club);
}
