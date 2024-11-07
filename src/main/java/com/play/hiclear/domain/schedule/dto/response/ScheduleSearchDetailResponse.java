package com.play.hiclear.domain.schedule.dto.response;

import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.schedule.entity.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ScheduleSearchDetailResponse {
    private Long id;
    private String email;
    private String title;
    private String description;
    private String regionAddress;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long createdBy;
    private List<Long> participants;
    private ClubResponse club;

    public static ScheduleSearchDetailResponse from(Schedule schedule, String email, Long createdBy, List<Long> participants) {
        ScheduleSearchDetailResponse response = new ScheduleSearchDetailResponse();
        response.id = schedule.getId();
        response.email = email;
        response.title = schedule.getTitle();
        response.description = schedule.getDescription();
        response.regionAddress = schedule.getRegionAddress();
        response.startTime = schedule.getStartTime();
        response.endTime = schedule.getEndTime();
        response.createdBy = createdBy;
        response.participants = participants;
        response.club = new ClubResponse(schedule.getClub());
        return response;
    }

    @Getter
    @NoArgsConstructor
    public static class ClubResponse {
        private Long id;
        private String clubName;

        public ClubResponse(Club club) {
            this.id = club.getId();
            this.clubName = club.getClubname();
        }
    }
}