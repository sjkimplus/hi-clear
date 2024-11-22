package com.play.hiclear.domain.schedule.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.schduleparticipant.entity.ScheduleParticipant;
import com.play.hiclear.domain.schedule.dto.request.ScheduleUpdateRequest;
import com.play.hiclear.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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