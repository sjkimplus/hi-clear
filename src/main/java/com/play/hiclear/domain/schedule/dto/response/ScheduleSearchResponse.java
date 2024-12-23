package com.play.hiclear.domain.schedule.dto.response;


import com.play.hiclear.domain.schedule.entity.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ScheduleSearchResponse {
    private Long id;
    private String title;
    private String description;
    private String regionAddress;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static ScheduleSearchResponse from(Schedule schedule) {
        ScheduleSearchResponse response = new ScheduleSearchResponse();
        response.id = schedule.getId();
        response.title = schedule.getTitle();
        response.description = schedule.getDescription();
        response.regionAddress = schedule.getRegionAddress();
        response.startTime = schedule.getStartTime();
        response.endTime = schedule.getEndTime();
        return response;
    }
}
