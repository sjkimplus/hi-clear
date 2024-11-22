package com.play.hiclear.domain.participant.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)

public enum ParticipantStatus {

    PENDING, REJECTED, ACCEPTED, CANCELED;

}
