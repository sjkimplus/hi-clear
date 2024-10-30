package com.play.hiclear.domain.participant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ParticipantStatus {

    PENDING, REJECTED, ACCEPTED, CANCELED;

}
