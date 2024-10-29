package com.play.hiclear.domain.timeslot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class TimeSlotResponse {

    private final Long gymId;
    private final Long courtNum;
    private final LocalTime startTime;
    private final LocalTime endTime;
}
