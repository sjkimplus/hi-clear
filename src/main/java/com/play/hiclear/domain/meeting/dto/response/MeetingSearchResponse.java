package com.play.hiclear.domain.meeting.dto.response;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.meeting.entity.Meeting;

import java.time.LocalDateTime;

public class MeetingSearchResponse {
    private Long id;
    private String title;
    private String region;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Ranks ranks;
    private int groupSize;
    private int numberJoined;

    public MeetingSearchResponse(Meeting meeting, int numberJoined) {
        this.id = meeting.getId();
        this.title = meeting.getTitle();
        this.region = meeting.getRegion();
        this.startTime = meeting.getStartTime();
        this.endTime = meeting.getEndTime();
        this.ranks = meeting.getRanks();
        this.groupSize = meeting.getGroupSize();
        this.numberJoined = numberJoined;
    }
}
