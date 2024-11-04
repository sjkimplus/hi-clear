package com.play.hiclear.domain.schedule.dto.response;

import com.play.hiclear.domain.reservation.dto.response.ReservationSearchResponse;
import com.play.hiclear.domain.reservation.entity.Reservation;
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
    private String region;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static ScheduleSearchResponse from(Schedule schedule) {
        ScheduleSearchResponse response = new ScheduleSearchResponse();
        response.id = schedule.getId();
        response.title = schedule.getTitle();
        response.description = schedule.getDescription();
        response.region = schedule.getRegion();
        response.startTime = schedule.getStartTime();
        response.endTime = schedule.getEndTime();
        return response;
    }
}
