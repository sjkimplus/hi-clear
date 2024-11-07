package com.play.hiclear.domain.meeting.entity;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateEditRequest;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

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


    public Meeting(MeetingCreateEditRequest request, User user, GeoCodeDocument address) {
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

    public void update(MeetingCreateEditRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.startTime = request.getStartTime();
        this.endTime = request.getEndTime();
        this.ranks = request.getRanks();
        this.groupSize = request.getGroupSize();
    }

    public void updateWithAddress(MeetingCreateEditRequest request, GeoCodeDocument address) {
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

    public void markFinished() {
        this.finished = true;
    }

}
