package com.play.hiclear.domain.reservation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReservationUpdateRequest {
    private Long timeId;
    private LocalDate date;

    public ReservationUpdateRequest(Long timeId, LocalDate date) {
        this.timeId = timeId;
        this.date = date;
    }
}