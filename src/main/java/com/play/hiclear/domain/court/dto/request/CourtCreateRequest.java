package com.play.hiclear.domain.court.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CourtCreateRequest {

    @Min(value = 1, message = "코트 넘버를 입력해주세요.")
    private long courtNum;

    @Min(value = 0)
    private int price;

    public CourtCreateRequest(long courtNum, int price) {
        this.courtNum = courtNum;
        this.price = price;
    }
}
