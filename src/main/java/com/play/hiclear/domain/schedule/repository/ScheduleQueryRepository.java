package com.play.hiclear.domain.schedule.repository;


import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.schedule.entity.Schedule;

import java.util.List;

public interface ScheduleQueryRepository {
    List<Schedule> findByClub(Club club);
}
