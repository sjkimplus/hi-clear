package com.play.hiclear.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GeoCodeResponse {
    @JsonProperty("documents")
    private List<GeoCodeDocument> documents; // 응답의 주요 데이터

    // Getter 및 Setter
    public List<GeoCodeDocument> getDocuments() {
        return documents;
    }
}