package com.play.hiclear.domain.timeslot.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class TimeSlotRequest {

    private LocalTime startTime;
}
