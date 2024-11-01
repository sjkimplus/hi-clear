package com.play.hiclear.domain.court.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CourtCreateRequest {
    private Long courtNum;
    private int price;

    public CourtCreateRequest(Long courtNum, int price) {
        this.courtNum = courtNum;
        this.price = price;
    }
}
