package com.play.hiclear.domain.participant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParticipantListResponse {
    private List<ParticipantResponse> joinedParticipants = new ArrayList<>();

    public ParticipantListResponse(List<ParticipantResponse> joinedParticipants) {
        this.joinedParticipants = joinedParticipants;
    }
}
