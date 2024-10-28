package com.play.hiclear.domain.participant.dto;

import com.play.hiclear.domain.participant.enums.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantUpdateRequest {
    private ParticipantStatus status;
}
