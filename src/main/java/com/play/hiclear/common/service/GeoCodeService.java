package com.play.hiclear.common.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.dto.response.GeoCodeResponse;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.logging.Logger;

@Slf4j
@Service
public class GeoCodeService {
    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/address.json?query=%s";

    @Value("${kakao.api.key}")
    private String KAKAO_APP_KEY;

    private final WebClient webClient;

    public GeoCodeService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(KAKAO_API_URL).build();
    }

    public GeoCodeDocument getGeoCode(String address) {
        String url = String.format(KAKAO_API_URL, address);

        try {
            GeoCodeResponse response = webClient.get()
                    .uri(url)
                    .headers(headers -> headers.set("Authorization", "KakaoAK " + KAKAO_APP_KEY))
                    .retrieve()
                    .bodyToMono(GeoCodeResponse.class)
                    .block();

            if (response != null && response.getDocuments() != null && !response.getDocuments().isEmpty()) {
                return response.getDocuments().get(0); // Return the first result
            }
        } catch (WebClientResponseException e) {
            log.info("API 호출 실패 {}", e.getMessage());
        }
        throw new CustomException(ErrorCode.ADDRESS_BAD_REQUEST);
    }

    public Point createPoint(GeoCodeDocument geoCodeDocument) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(geoCodeDocument.getLongitude(), geoCodeDocument.getLatitude()));
        point.setSRID(4326);
        return point;
    }
}

