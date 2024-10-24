package com.play.hiclear.domain.alarm.repository;

import com.play.hiclear.domain.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
