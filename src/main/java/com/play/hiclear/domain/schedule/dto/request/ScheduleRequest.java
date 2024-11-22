package com.play.hiclear.domain.schedule.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ScheduleRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String address;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
    @NotNull
    private List<Long> participants;

    public ScheduleRequest(String title, String description, String address, LocalDateTime startTime, LocalDateTime endTime, List<Long> participants) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = participants;
    }
}
