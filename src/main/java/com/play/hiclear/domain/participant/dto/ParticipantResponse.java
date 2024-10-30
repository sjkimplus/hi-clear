package com.play.hiclear.domain.participant.dto;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.participant.entity.Participant;
import com.play.hiclear.domain.participant.enums.ParticipantRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ParticipantResponse {
    private Long id;
    private String name;
    private Ranks rank;
//    private String stringTension;
    private ParticipantRole role;

    public ParticipantResponse(Participant participant) {
        this.id = participant.getId();
        this.name = participant.getUser().getName();
        this.rank = participant.getUser().getSelfRank();
        this.role = participant.getRole();
    }
}
