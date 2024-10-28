package com.play.hiclear.domain.timeslot.repository;

import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
}
