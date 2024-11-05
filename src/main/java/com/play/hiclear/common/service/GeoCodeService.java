package com.play.hiclear.common.service;

import com.play.hiclear.common.dto.response.GeoCodeAddress;
import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.dto.response.GeoCodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class GeoCodeService {
    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/address.json?query=%s";

    @Value("${kakao.api.key}")
    private String KAKAO_APP_KEY;

    public GeoCodeAddress getGeoCode(String address) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(KAKAO_API_URL, address);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_APP_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        GeoCodeResponse response = restTemplate.exchange(url, HttpMethod.GET, entity, GeoCodeResponse.class).getBody();

        if (response != null && response.getDocuments() != null && !response.getDocuments().isEmpty()) {
            GeoCodeDocument document = response.getDocuments().get(0);

            Double latitude = document.getAddress().getLatitude();
            Double longitude = document.getAddress().getLongitude();
            String regionAddress = document.getAddress().getAddressName();
            String roadAddress = document.getRoadAddress().getAddressName(); // 도로명 주소를 가져옵니다.

            return new GeoCodeAddress(latitude, longitude, regionAddress, roadAddress);
        }
        return null; // 결과가 없을 경우 null 반환
    }
}