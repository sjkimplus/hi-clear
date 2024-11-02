package com.play.hiclear.domain.reservation.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationChangeStatusRequest {
    @NotBlank
    private String status;

    public ReservationChangeStatusRequest(String status) {
        this.status = status;
    }
}
