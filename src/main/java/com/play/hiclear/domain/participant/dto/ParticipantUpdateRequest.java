package com.play.hiclear.domain.participant.dto;

import com.play.hiclear.domain.participant.enums.ParticipantStatus;
import lombok.Getter;

@Getter
public class ParticipantUpdateRequest {
    private ParticipantStatus status;

    public ParticipantUpdateRequest(ParticipantStatus status) {
        this.status = status;
    }
}
