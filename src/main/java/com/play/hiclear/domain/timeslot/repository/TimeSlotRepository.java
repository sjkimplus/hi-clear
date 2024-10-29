package com.play.hiclear.domain.timeslot.repository;

import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findAllByCourt_CourtNum(Long courtNum);
    Optional<TimeSlot> findByStartTimeAndCourt_CourtNum(LocalTime startTime, Long courtNum);
}
