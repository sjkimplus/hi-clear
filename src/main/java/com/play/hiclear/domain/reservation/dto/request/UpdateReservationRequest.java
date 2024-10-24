package com.play.hiclear.domain.reservation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateReservationRequest {
    private Long timeId;

    public UpdateReservationRequest(Long timeId) {
        this.timeId = timeId;
    }
}