package com.play.hiclear.domain.reservation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationUpdateRequest {
    private Long timeId;

    public ReservationUpdateRequest(Long timeId) {
        this.timeId = timeId;
    }
}