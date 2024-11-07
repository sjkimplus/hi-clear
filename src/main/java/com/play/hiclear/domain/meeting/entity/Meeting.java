package com.play.hiclear.domain.meeting.entity;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateRequest;
import com.play.hiclear.domain.meeting.dto.request.MeetingEditRequest;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Meeting extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    private String regionAddress;

    private String roadAddress;

    private Double longitude;
    private Double latitude;

    private String content;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ranks ranks;

    private int groupSize;

    private boolean finished;

    public Meeting(MeetingCreateRequest request, User user, GeoCodeDocument address) {
        this.user = user;
        this.title = request.getTitle();
        this.regionAddress = address.getRegionAddress();
        this.roadAddress = address.getRoadAddress();
        this.longitude = address.getLongitude();
        this.latitude = address.getLatitude();
        this.content = request.getContent();
        this.startTime = request.getStartTime();
        this.endTime = request.getEndTime();
        this.ranks = request.getRanks();
        this.groupSize = request.getGroupSize();
    }


    // 모임 일정 수정
    public void update(MeetingEditRequest request) {
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            this.title = request.getTitle();
        }
        if (request.getContent() != null && !request.getContent().isEmpty()) {
            this.content = request.getContent();
        }
        if (request.getStartTime() != null) {
            this.startTime = request.getStartTime();
        }
        if (request.getEndTime() != null) {
            this.endTime = request.getEndTime();
        }
        if (request.getRanks() != null) {
            this.ranks = request.getRanks();
        }
        if (request.getGroupSize() >= 4 && request.getGroupSize()!=this.groupSize){
            this.groupSize = request.getGroupSize();
        }
    }

    public void updateLocation(GeoCodeDocument address) {
        this.regionAddress = address.getRegionAddress();
        this.roadAddress = address.getRoadAddress();
        this.longitude = address.getLongitude();
        this.latitude = address.getLatitude();
    }

    public void markFinished() {
        this.finished = true;
    }

}
