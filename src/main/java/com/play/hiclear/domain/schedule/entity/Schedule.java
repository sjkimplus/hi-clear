package com.play.hiclear.domain.schedule.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.schduleparticipant.entity.ScheduleParticipant;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
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

    @OneToOne
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
    public void updateSchedule(String title, String description, String region, LocalDateTime startTime, LocalDateTime endTime) {
        if (title != null && !title.isEmpty()) {
            this.title = title;
        }
        if (description != null && !description.isEmpty()) {
            this.description = description;
        }
        if (region != null && !region.isEmpty()) {
            this.region = region;
        }
        if (startTime != null) {
            this.startTime = startTime;
        }
        if (endTime != null) {
            this.endTime = endTime;
        }
    }
}