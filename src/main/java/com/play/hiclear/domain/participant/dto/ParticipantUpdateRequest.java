package com.play.hiclear.domain.participant.dto;

import com.play.hiclear.domain.participant.enums.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantUpdateRequest {
    private ParticipantStatus status;
}
