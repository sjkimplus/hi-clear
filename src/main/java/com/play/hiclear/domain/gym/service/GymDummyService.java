package com.play.hiclear.domain.gym.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GymDummyService {

    private final GymRepository gymRepository;
    private final UserRepository userRepository;

    private final int scale = 6;  // 소수점 자리 수 (예시: 6자리)
    private final BigDecimal liatMin = new BigDecimal("33.0");  // 최소값
    private final BigDecimal latMax = new BigDecimal("38.6");  // 최대값
    private final BigDecimal longMin = new BigDecimal("124.6");  // 최소 경도
    private final BigDecimal longMax = new BigDecimal("131.0");  // 최대 경도
    private final BCryptPasswordEncoder passwordEncoder;
    private final String[] region = {"서울", "부산", "대구", "인천", "대전", "괴산"};
    private final Random random = new Random();

    @PostConstruct
    @Transactional
    public void generateDummyDataWithEvenDistribution() {
        // 데이터베이스에 더미 데이터가 이미 존재하면 생성하지 않음
        if (gymRepository.count() > 0) {
            System.out.println("더미 데이터가 이미 존재합니다.");
            return;
        }

        // 사용자 생성
        String encodePassword = passwordEncoder.encode("A1234567*");
        User user = new User("이름", "adminuser1@gmail.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", createPoint(126.977829174031, 37.5663174209601), encodePassword, Ranks.RANK_A, UserRole.BUSINESS);
        userRepository.save(user);

        // 일정 간격으로 위도와 경도 생성
        int gridSize = 100;  // 위경도 분포의 세밀도 조절
        BigDecimal latitudeStep = latMax.subtract(liatMin).divide(BigDecimal.valueOf(gridSize), scale, RoundingMode.HALF_UP);
        BigDecimal longitudeStep = longMax.subtract(longMin).divide(BigDecimal.valueOf(gridSize), scale, RoundingMode.HALF_UP);

        IntStream.range(0, gridSize).forEach(i -> {
            IntStream.range(0, gridSize).forEach(j -> {
                String regionAddress = region[random.nextInt(region.length)];
                String name = regionAddress + (i < gridSize / 2 ? " 공공체육관" : " 사설체육관");

                BigDecimal latitude = liatMin.add(latitudeStep.multiply(BigDecimal.valueOf(i))).setScale(scale, RoundingMode.HALF_UP);
                BigDecimal longitude = longMin.add(longitudeStep.multiply(BigDecimal.valueOf(j))).setScale(scale, RoundingMode.HALF_UP);

                GymType gymType = (i < gridSize / 2) ? GymType.PUBLIC : GymType.PRIVATE;
                Gym gym = new Gym(name, null, regionAddress, null, createPoint(longitude.doubleValue(), latitude.doubleValue()), gymType, user);

                gymRepository.save(gym);

                if ((i * gridSize + j) % 2500 == 0) {
                    gymRepository.flush();
                }
            });
        });
    }

    private Point createPoint(Double longitude, Double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
    }
}
