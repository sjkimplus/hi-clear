package com.play.hiclear.domain.schedule.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleUpdateRequest {
    private String title;
    private String description;
    private String address;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public ScheduleUpdateRequest(String title, String description, String address, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
