package com.play.hiclear.domain.meeting.dto.response;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.participant.dto.ParticipantResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MyMeetingDetailResponse {

    private Long id;
    private String title;
    private String region;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Ranks ranks;
    private int groupSize;
    private int numberJoined;
    private List<ParticipantResponse> pendingParticipants;


    public MyMeetingDetailResponse(Meeting meeting, int numberJoined, List<ParticipantResponse> pendingParticipants) {
        this.id = meeting.getId();
        this.title = meeting.getTitle();
        this.region = meeting.getRegionAddress();
        this.startTime = meeting.getStartTime();
        this.endTime = meeting.getEndTime();
        this.ranks = meeting.getRanks();
        this.groupSize = meeting.getGroupSize();
        this.numberJoined = numberJoined;
        this.pendingParticipants = pendingParticipants;


    }
}
