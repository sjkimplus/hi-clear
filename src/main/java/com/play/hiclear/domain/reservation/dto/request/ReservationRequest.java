package com.play.hiclear.domain.reservation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReservationRequest {
    private List<Long> timeList;
    private Long courtId;
    private LocalDate date;

    public ReservationRequest(List<Long> timeList, Long courtId, LocalDate date) {
        this.timeList = timeList;
        this.courtId = courtId;
        this.date = date;
    }
}
