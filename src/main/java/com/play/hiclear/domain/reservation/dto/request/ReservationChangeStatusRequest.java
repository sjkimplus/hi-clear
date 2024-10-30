package com.play.hiclear.domain.reservation.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationChangeStatusRequest {
    private String status;

    public ReservationChangeStatusRequest(String status) {
        this.status = status;
    }
}
