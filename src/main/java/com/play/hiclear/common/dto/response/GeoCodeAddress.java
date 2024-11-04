package com.play.hiclear.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GeoCodeAddress {
    @JsonProperty("y")
    private final Double latitude; // 위도

    @JsonProperty("x")
    private final Double longitude; // 경도

    @JsonProperty("address_name")
    private final String addressName; // 전체 도로명 주소

    // 생성자 추가
    public GeoCodeAddress(Double latitude, Double longitude, String addressName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressName = addressName;
    }
}