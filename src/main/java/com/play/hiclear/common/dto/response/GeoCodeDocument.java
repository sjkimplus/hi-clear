package com.play.hiclear.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GeoCodeDocument {

    // Primary fields at the top level of the document
    @JsonProperty("address")
    private NestedAddress1 regionAddress;

    @JsonProperty("x")
    private String longitude;

    @JsonProperty("y")
    private String latitude;

    @JsonProperty("road_address")
    private NestedAddress2 roadAddress;

    // test에 사용
    public GeoCodeDocument(double longitude, double latitude, String regionAddressName, String roadAddressName) {
        this.longitude = String.valueOf(longitude);
        this.latitude = String.valueOf(latitude);

        this.regionAddress = new NestedAddress1();
        this.regionAddress.addressName = regionAddressName;
        this.roadAddress = new NestedAddress2();
        this.roadAddress.addressName = roadAddressName;
    }

    public String getRegionAddress() {
        return regionAddress != null ? regionAddress.getAddressName() : null ;
    }

    public String getRoadAddress() {
        return roadAddress != null ? roadAddress.getAddressName() : null;
    }

    public Double getLatitude() {
        return latitude != null ? Double.parseDouble(latitude) : null;
    }

    public Double getLongitude() {
        return longitude != null ? Double.parseDouble(longitude) : null;
    }

    // Nested class to handle different address types
    public static class NestedAddress1 {
        @JsonProperty("address_name")
        private String addressName;

        public String getAddressName() {
            return addressName;
        }
    }

    // Nested class to handle different address types
    public static class NestedAddress2 {
        @JsonProperty("address_name")
        private String addressName;

        public String getAddressName() {
            return addressName;
        }
    }
}
