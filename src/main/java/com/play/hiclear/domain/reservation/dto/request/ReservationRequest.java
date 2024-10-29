package com.play.hiclear.domain.reservation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReservationRequest {
    private List<Long> timeList;
    private Long courtId;

    public ReservationRequest(List<Long> timeList, Long courtId) {
        this.timeList = timeList;
        this.courtId = courtId;
    }
}
