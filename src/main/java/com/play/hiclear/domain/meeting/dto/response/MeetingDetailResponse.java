package com.play.hiclear.domain.meeting.dto.response;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.meeting.entity.Meeting;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class MeetingDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String region;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Ranks ranks;
    private int numberJoined;

    public MeetingDetailResponse(Meeting meeting, int numberJoined) {
        this.id = meeting.getId();
        this.title = meeting.getTitle();
        this.content = meeting.getContent();
        this.region = meeting.getRegion();
        this.startTime = meeting.getStartTime();
        this.endTime = meeting.getEndTime();
        this.ranks = meeting.getRanks();
        this.numberJoined = numberJoined;
    }
}
