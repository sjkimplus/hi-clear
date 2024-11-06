package com.play.hiclear.domain.court.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CourtUpdateRequest {
    private Integer price;

    public CourtUpdateRequest(Integer price) {
        if (price != null) {
            this.price = price;
        }
    }
}
