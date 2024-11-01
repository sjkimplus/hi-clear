package com.play.hiclear.domain.timeslot.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findAllByCourt_CourtNum(Long courtNum);
    Optional<TimeSlot> findByStartTimeAndCourt_CourtNum(LocalTime startTime, Long courtNum);

    default TimeSlot findByStartTimeAndCourt_CourtNumOrThrow(LocalTime startTime, Long courtNum){
        return findByStartTimeAndCourt_CourtNum(startTime, courtNum)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, TimeSlot.class.getSimpleName()));
    }
}
