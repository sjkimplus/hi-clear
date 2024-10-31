package com.play.hiclear.domain.meeting.dto.request;

import com.play.hiclear.common.enums.Ranks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class MeetingCreateEditRequest {
    private String title;
    private String region;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Ranks ranks;
    private int groupSize;

    public MeetingCreateEditRequest(String title, String region, String content, LocalDateTime now, LocalDateTime futureTime, Ranks ranks, int i) {
        this.title = title;
        this.region = region;
        this.content = content;
        this.startTime = now;
        this.endTime = futureTime;
        this.ranks = ranks;
        this.groupSize = i;
    }
}
