package com.play.hiclear.domain.meeting.dto.response;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.meeting.entity.Meeting;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class MeetingSearchResponse {
    private Long id;
    private String title;
    private String regionAddress;
    private double longitude;
    private double latitude;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Ranks ranks;
    private int groupSize;
    private long numberJoined;

    public MeetingSearchResponse(Meeting meeting, long numberJoined) {
        this.id = meeting.getId();
        this.title = meeting.getTitle();
        this.regionAddress = meeting.getRegionAddress();
        this.longitude = meeting.getLongitude();
        this.latitude = meeting.getLatitude();
        this.startTime = meeting.getStartTime();
        this.endTime = meeting.getEndTime();
        this.ranks = meeting.getRanks();
        this.groupSize = meeting.getGroupSize();
        this.numberJoined = numberJoined;
    }
}
