package com.play.hiclear.domain.court.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CourtUpdateRequest {

    @Min(value = 0)
    private int price;

    public CourtUpdateRequest(int price) {
        this.price = price;
    }
}
