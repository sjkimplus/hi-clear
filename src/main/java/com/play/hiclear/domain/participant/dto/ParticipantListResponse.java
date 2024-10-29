package com.play.hiclear.domain.participant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ParticipantListResponse {
    private List<ParticipantResponse> joinedParticipants;

    public ParticipantListResponse(List<ParticipantResponse> joinedParticipants) {
        this.joinedParticipants = joinedParticipants;
    }
}
