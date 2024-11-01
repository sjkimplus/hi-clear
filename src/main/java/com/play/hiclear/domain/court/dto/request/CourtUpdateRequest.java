package com.play.hiclear.domain.court.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CourtUpdateRequest {
    private int price;

    public CourtUpdateRequest(int price) {
        this.price = price;
    }
}
