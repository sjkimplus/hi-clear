package com.play.hiclear.domain.court.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourtSearchResponse {
    private final long courtNum;
    private final int price;
}
