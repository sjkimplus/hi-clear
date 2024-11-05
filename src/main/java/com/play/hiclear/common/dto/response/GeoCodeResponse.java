package com.play.hiclear.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class GeoCodeResponse {
    @JsonProperty("documents")
    private List<GeoCodeDocument> documents; // 응답의 주요 데이터
}