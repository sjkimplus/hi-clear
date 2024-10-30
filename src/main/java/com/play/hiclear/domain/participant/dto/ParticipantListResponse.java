package com.play.hiclear.domain.participant.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParticipantListResponse {
    private List<ParticipantResponse> joinedParticipants = new ArrayList<>();

    public ParticipantListResponse(List<ParticipantResponse> joinedParticipants) {
        this.joinedParticipants = joinedParticipants;
    }
}
