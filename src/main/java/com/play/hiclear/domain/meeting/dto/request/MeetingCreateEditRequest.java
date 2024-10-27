package com.play.hiclear.domain.meeting.dto.request;

import com.play.hiclear.common.enums.Ranks;
import lombok.Getter;
import org.springframework.data.geo.Point;

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
}
