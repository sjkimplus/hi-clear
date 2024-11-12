package com.play.hiclear.common.utils;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.service.GeoCodeService;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class DistanceCalculator {

    private static final BigDecimal EARTH_RADIUS = new BigDecimal("6371"); // Kilometers
    private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);
    private final GeoCodeService geoCodeService;

    public DistanceCalculator(GeoCodeService geoCodeService) {
        this.geoCodeService = geoCodeService;
    }

    // 각도를 라디안으로 변경(BigDecimal)
    private BigDecimal toRadians(BigDecimal degrees) {
        BigDecimal pi = new BigDecimal(Math.PI);
        return degrees.multiply(pi, MATH_CONTEXT).divide(new BigDecimal("180"), MATH_CONTEXT);
    }

    // 해당 각도의 sin 근사치 값 계산
    private BigDecimal sin(BigDecimal radians) {
        return new BigDecimal(Math.sin(radians.doubleValue()), MATH_CONTEXT);
    }

    // 해당 각도의 cos 근사치 값 계산
    private BigDecimal cos(BigDecimal radians) {
        return new BigDecimal(Math.cos(radians.doubleValue()), MATH_CONTEXT);
    }

    // 해당 각도의 arccosine 근사치 값 계산
    private BigDecimal acos(BigDecimal value) {
        return new BigDecimal(Math.acos(value.doubleValue()), MATH_CONTEXT);
    }

    // 거리계산
    public BigDecimal calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        BigDecimal bigLat1 = BigDecimal.valueOf(lat1);
        BigDecimal bigLon1 = BigDecimal.valueOf(lon1);
        BigDecimal bigLat2 = BigDecimal.valueOf(lat2);
        BigDecimal bigLon2 = BigDecimal.valueOf(lon2);
        BigDecimal lat1Rad = toRadians(bigLat1);
        BigDecimal lon1Rad = toRadians(bigLon1);
        BigDecimal lat2Rad = toRadians(bigLat2);
        BigDecimal lon2Rad = toRadians(bigLon2);

        BigDecimal sinLat1 = sin(lat1Rad);
        BigDecimal sinLat2 = sin(lat2Rad);
        BigDecimal cosLat1 = cos(lat1Rad);
        BigDecimal cosLat2 = cos(lat2Rad);
        BigDecimal deltaLon = lon1Rad.subtract(lon2Rad).abs();
        BigDecimal cosDeltaLon = cos(deltaLon);

        // 하버사인 공식 구성요소
        BigDecimal centralAngle = sinLat1.multiply(sinLat2)
                .add(cosLat1.multiply(cosLat2).multiply(cosDeltaLon));
        BigDecimal distance = EARTH_RADIUS.multiply(acos(centralAngle), MATH_CONTEXT);

        return distance;
    }
}

