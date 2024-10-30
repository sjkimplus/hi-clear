package com.play.hiclear.domain.schedule.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.schduleparticipant.entity.ScheduleParticipant;
import com.play.hiclear.domain.schedule.dto.request.ScheduleRequest;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "schedules")
public class Schedule extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;                               // 제목

    private String description;                         // 설명

    private String region;                              // 장소

    private LocalDateTime startTime;                    // 모임 일정 시작시간

    private LocalDateTime endTime;                      // 모임 일정 시작시간

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;                                  // 모임

    @OneToMany(mappedBy = "schedule")
    private List<ScheduleParticipant> scheduleParticipants;         // 참가자 목록

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Schedule(User user, Club club, String title, String description, String region, LocalDateTime startTime, LocalDateTime endTime) {
        this.user = user;
        this.club = club;
        this.title = title;
        this.description = description;
        this.region = region;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // 모임 일정 수정
    public void updateSchedule(ScheduleRequest scheduleRequestDto) {
        if (scheduleRequestDto.getTitle() != null && !scheduleRequestDto.getTitle().isEmpty()) {
            this.title = scheduleRequestDto.getTitle();
        }
        if (scheduleRequestDto.getDescription() != null && !scheduleRequestDto.getDescription().isEmpty()) {
            this.description = scheduleRequestDto.getDescription();
        }
        if (scheduleRequestDto.getRegion() != null && !scheduleRequestDto.getRegion().isEmpty()) {
            this.region = scheduleRequestDto.getRegion();
        }
        if (scheduleRequestDto.getStartTime() != null) {
            this.startTime = scheduleRequestDto.getStartTime();
        }
        if (scheduleRequestDto.getEndTime() != null) {
            this.endTime = scheduleRequestDto.getEndTime();
        }
    }
}