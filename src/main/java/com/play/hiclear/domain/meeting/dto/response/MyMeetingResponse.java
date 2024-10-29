package com.play.hiclear.domain.meeting.dto.response;

import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.participant.enums.ParticipantStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MyMeetingResponse {

    private Long id;
    private String title;
    private String region;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ParticipantStatus status;


    public MyMeetingResponse(Meeting meeting) {
        this.id = meeting.getId();
        this.title = meeting.getTitle();
        this.region = meeting.getRegion();
        this.startTime = meeting.getStartTime();
        this.endTime = meeting.getEndTime();
    }

    public MyMeetingResponse(Meeting meeting, ParticipantStatus status) {
        this.id = meeting.getId();
        this.title = meeting.getTitle();
        this.region = meeting.getRegion();
        this.startTime = meeting.getStartTime();
        this.endTime = meeting.getEndTime();
        this.status = status;
    }
}
