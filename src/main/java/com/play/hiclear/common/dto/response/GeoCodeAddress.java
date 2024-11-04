package com.play.hiclear.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GeoCodeAddress {
    @JsonProperty("y")
    private Double latitude; // 위도

    @JsonProperty("x")
    private Double longitude; // 경도

    @JsonProperty("road_address")
    private String roadAddress; // 도로명 주소

    @JsonProperty("address_name")
    private String addressName; // 일반 주소
}