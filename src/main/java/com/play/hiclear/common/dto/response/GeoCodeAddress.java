package com.play.hiclear.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GeoCodeAddress {
    @JsonProperty("y")
    private Double latitude; // 위도

    @JsonProperty("x")
    private Double longitude; // 경도

    @JsonProperty("address_name")
    private String addressName; // 전체 도로명 주소

    private final String regionAddress;

    private final String roadAddress;

    // 생성자 추가
    public GeoCodeAddress(Double latitude, Double longitude, String regionAddress, String roadAddress) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.regionAddress = regionAddress;
        this.roadAddress = roadAddress;
    }
}