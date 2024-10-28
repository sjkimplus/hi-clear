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
    private String email; // 작성자 이메일
    private String title;
    private String description;
    private String region;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long createdBy; // 작성자 ID
    private List<Long> participants; // 참가자 ID 목록
    private ClubResponse club; // 클럽 정보

    public static ScheduleSearchDetailResponse from(Schedule schedule, String email, Long createdBy, List<Long> participants) {
        ScheduleSearchDetailResponse response = new ScheduleSearchDetailResponse();
        response.id = schedule.getId();
        response.email = email;
        response.title = schedule.getTitle();
        response.description = schedule.getDescription();
        response.region = schedule.getRegion();
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