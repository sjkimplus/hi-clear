package com.play.hiclear.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GeoCodeDocument {
    @JsonProperty("address")
    private GeoCodeAddress address; // 주소 정보

    public String getRoadAddress() {
        return address != null ? address.getAddressName() : null;
    }
}