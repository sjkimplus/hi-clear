package com.play.hiclear.domain.participant.dto;

import com.play.hiclear.domain.participant.enums.ParticipantStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ParticipantUpdateRequest {
    @NotNull
    private ParticipantStatus status;

    public ParticipantUpdateRequest(ParticipantStatus status) {
        this.status = status;
    }
}
