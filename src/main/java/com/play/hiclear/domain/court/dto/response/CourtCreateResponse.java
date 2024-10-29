package com.play.hiclear.domain.court.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourtCreateResponse {
    private final Long courtNum;
    private final int price;
}
