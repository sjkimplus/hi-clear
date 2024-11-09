package com.play.hiclear.domain.gym.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

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
    public void generateDummyData() {

        // 데이터베이스에 더미 데이터가 이미 존재하면 생성하지 않음
        if (gymRepository.count() > 0) {
            System.out.println("더미 데이터가 이미 존재합니다.");
            return;
        }

        //더미 데이터 생성
        String encodePassword = passwordEncoder.encode("A1234567*");
        User user = new User("이름", "adminuser1@gmail.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, encodePassword, Ranks.RANK_A, UserRole.BUSINESS);
        userRepository.save(user);

        for (int i = 0; i < 25000; i++) {
            int regionNum = random.nextInt(region.length);
            String regionAddress = region[regionNum];
            String name = regionAddress + " 공공체육관";
            Double latitude = generateRandomBigDecimal(liatMin, latMax, scale).doubleValue();
            Double longitude = generateRandomBigDecimal(longMin, longMax, scale).doubleValue();

            // Gym 객체 생성 후 저장
            Gym gym = new Gym(name, null, regionAddress, null, latitude, longitude, GymType.PUBLIC, user);
            gymRepository.save(gym);

            if (i % 1000 == 0) {
                gymRepository.flush();
            }
        }

        for (int i = 0; i < 25000; i++) {
            int regionNum = random.nextInt(region.length);
            String regionAddress = region[regionNum];
            String name = regionAddress + " 사설체육관";
            Double latitude = generateRandomBigDecimal(liatMin, latMax, scale).doubleValue();
            Double longitude = generateRandomBigDecimal(longMin, longMax, scale).doubleValue();

            // Gym 객체 생성 후 저장
            Gym gym = new Gym(name, null, regionAddress, null, latitude, longitude, GymType.PRIVATE, user);
            gymRepository.save(gym);

            if (i % 1000 == 0) {
                gymRepository.flush();
            }
        }
    }

    public static BigDecimal generateRandomBigDecimal(BigDecimal min, BigDecimal max, int scale) {
        Random random = new Random();

        // min과 max 범위 사이에서 랜덤 값 생성
        double randomValue = min.doubleValue() + (max.doubleValue() - min.doubleValue()) * random.nextDouble();

        // BigDecimal로 변환 후 scale 적용
        BigDecimal randomBigDecimal = new BigDecimal(randomValue).setScale(scale, RoundingMode.HALF_UP);

        return randomBigDecimal;
    }
}
