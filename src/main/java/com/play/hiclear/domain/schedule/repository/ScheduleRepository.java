package com.play.hiclear.domain.schedule.repository;

import com.play.hiclear.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleQueryRepository {
}
