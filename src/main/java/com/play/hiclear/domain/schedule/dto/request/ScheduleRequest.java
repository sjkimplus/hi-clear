package com.play.hiclear.domain.schedule.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ScheduleRequest {
    private String title;
    private String description;
    private String region;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Long> participants;
}
