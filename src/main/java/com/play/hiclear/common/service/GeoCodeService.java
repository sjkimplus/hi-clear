package com.play.hiclear.common.service;

import com.play.hiclear.common.dto.response.GeoCodeAddress;
import com.play.hiclear.common.dto.response.GeoCodeResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoCodeService {
    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/address.json?query=%s";
    private static final String KAKAO_APP_KEY = "5e754926c467932c64030b8215c00a58"; // 발급받은 키를 입력하세요.

    public GeoCodeAddress getGeoCode(String address) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(KAKAO_API_URL, address);

        // 헤더에 인증 정보 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_APP_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // API 호출
        GeoCodeResponse response = restTemplate.exchange(url, HttpMethod.GET, entity, GeoCodeResponse.class).getBody();

        if (response != null && response.getDocuments() != null && !response.getDocuments().isEmpty()) {
            return response.getDocuments().get(0).getAddress(); // GeoCodeAddress 객체 반환
        }
        return null; // 결과가 없을 경우 null 반환
    }
}