package com.play.hiclear.domain.schedule.dto.response;

import com.play.hiclear.domain.schedule.entity.Schedule;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ClubScheduleResponse {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String clubname;

    public ClubScheduleResponse(Schedule schedule) {
        this.id = schedule.getId();
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
        this.clubname = schedule.getClub().getClubname();
    }
}
