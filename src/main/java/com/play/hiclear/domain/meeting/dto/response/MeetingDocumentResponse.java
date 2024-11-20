package com.play.hiclear.domain.meeting.dto.response;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.meeting.entity.Meeting;
import lombok.Getter;

@Getter
public class MeetingDocumentResponse {

    private Long id;

    private String title;

    private String regionAddress;

    private Ranks ranks;

    public MeetingDocumentResponse(Meeting meeting) {
        this.id = meeting.getId();
        this.title = meeting.getTitle();
        this.regionAddress = meeting.getRegionAddress();
        this.ranks = meeting.getRanks();
    }
}