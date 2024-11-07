package com.play.hiclear.domain.schedule.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.schduleparticipant.entity.ScheduleParticipant;
import com.play.hiclear.domain.schedule.dto.request.ScheduleUpdateRequest;
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

    private String regionAddress;                       // 일반 주소

    private String roadAddress;                         // 도로명 주소

    private LocalDateTime startTime;                    // 모임 일정 시작시간

    private LocalDateTime endTime;                      // 모임 일정 시작시간

    private Double latitude;                            // 위도
    private Double longitude;                           // 경도

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;                                  // 모임

    @OneToMany(mappedBy = "schedule")
    private List<ScheduleParticipant> scheduleParticipants;         // 참가자 목록

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Schedule(User user, Club club, String title, String description, LocalDateTime startTime, LocalDateTime endTime, String regionAddress, String roadAddress, Double latitude, Double longitude) {
        this.user = user;
        this.club = club;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.regionAddress = regionAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.roadAddress = roadAddress;
    }

    // 모임 일정 수정
    public void updateSchedule(ScheduleUpdateRequest scheduleUpdateRequest) {
        if (scheduleUpdateRequest.getTitle() != null && !scheduleUpdateRequest.getTitle().isEmpty()) {
            this.title = scheduleUpdateRequest.getTitle();
        }
        if (scheduleUpdateRequest.getDescription() != null && !scheduleUpdateRequest.getDescription().isEmpty()) {
            this.description = scheduleUpdateRequest.getDescription();
        }
        if (scheduleUpdateRequest.getStartTime() != null) {
            this.startTime = scheduleUpdateRequest.getStartTime();
        }
        if (scheduleUpdateRequest.getEndTime() != null) {
            this.endTime = scheduleUpdateRequest.getEndTime();
        }
    }

    // 모임 일정 위치 정보 수정
    public void updateLocation(String regionAddress, String roadAddress, Double latitude, Double longitude) {
        this.regionAddress = regionAddress;
        this.roadAddress = roadAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}