package com.play.hiclear.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

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
