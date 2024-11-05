package com.play.hiclear.common.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.dto.response.GeoCodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoCodeService {
    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/address.json?query=%s";

    @Value("${kakao.api.key}")
    private String KAKAO_APP_KEY;

    public GeoCodeDocument getGeoCode(String address) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(KAKAO_API_URL, address);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_APP_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            GeoCodeResponse response = restTemplate.exchange(url, HttpMethod.GET, entity, GeoCodeResponse.class).getBody();

            if (response != null && response.getDocuments() != null && !response.getDocuments().isEmpty()) {
                return response.getDocuments().get(0); // Return the first result
            }
        } catch (HttpClientErrorException e) {
            System.err.println("GeoCode API call failed: " + e.getMessage());
        }

        return null;
    }
}
