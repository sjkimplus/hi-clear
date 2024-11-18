package com.play.hiclear.domain.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReservationRequest {
    @NotNull
    private List<Long> timeList;
    @NotNull
    private Long courtId;
    @NotNull
    private LocalDate date;

    public ReservationRequest(List<Long> timeList, Long courtId, LocalDate date) {
        this.timeList = timeList;
        this.courtId = courtId;
        this.date = date;
    }
}
