package com.play.hiclear.domain.schedule.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScheduleUpdateRequest {
    private String title;
    private String description;
    private String address;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Long> participants;

    public ScheduleUpdateRequest(String title, String description, String address, LocalDateTime startTime, LocalDateTime endTime, List<Long> participants) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = participants;
    }
}
