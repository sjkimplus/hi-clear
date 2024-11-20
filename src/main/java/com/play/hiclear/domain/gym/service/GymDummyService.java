package com.play.hiclear.domain.gym.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
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
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 체육관 더미데이터 생성
     * @return 체육관 더미데이터
     */
    @Transactional
    public int generateDummyGyms() {
        // 데이터베이스에 더미 데이터가 이미 존재하면 생성하지 않음
        if (gymRepository.count() > 0 || userRepository.count() > 0) {
            throw new CustomException(ErrorCode.DUMMY_ALREADY_EXIST);
        }

        // 사용자 생성
        int scale = 8;  // 소수점 자리 수 (예시: 6자리)
        BigDecimal latMin = new BigDecimal("33.0");  // 최소값
        BigDecimal latMax = new BigDecimal("38.6");  // 최대값
        BigDecimal longMin = new BigDecimal("124.6");  // 최소 경도
        BigDecimal longMax = new BigDecimal("131.0");  // 최대 경도
        String[] region = {"서울", "부산", "대구", "인천", "대전", "괴산"};
        Random random = new Random();
        String encodePassword = passwordEncoder.encode("A1234567*");
        Point userPoint = createPoint(126.977829174031, 37.5663174209601);
        userPoint.setSRID(4326);
        User user = new User("이름", "adminuser1@gmail.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", userPoint, encodePassword, Ranks.RANK_A, UserRole.BUSINESS);
        userRepository.save(user);

        int i = 0;
        int count = 100000;
        while (i < count){
            // 지역 이름 랜덤 선택
            String regionAddress = region[random.nextInt(region.length)];
            String name = regionAddress + (i / 2 == 0 ? " 공공체육관" : " 사설체육관");

            // 무작위 위도, 경도 생성
            BigDecimal latitude = generateRandomBigDecimal(latMin, latMax, scale);
            BigDecimal longitude = generateRandomBigDecimal(longMin, longMax, scale);

            Point point = createPoint(longitude.doubleValue(), latitude.doubleValue());
            point.setSRID(4326);

            GymType gymType = (i / 2 == 0) ? GymType.PUBLIC : GymType.PRIVATE;
            Gym gym = new Gym(name, null, regionAddress, null, point, gymType, user);

            gymRepository.save(gym);

            if (i % 2500 == 0) {
                gymRepository.flush();
            }
            i++;
        };
        return count;
    };


    private Point createPoint(Double longitude, Double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
    }

    private static BigDecimal generateRandomBigDecimal(BigDecimal min, BigDecimal max, int scale) {
        Random random = new Random();

        // min과 max 범위 사이에서 랜덤 값 생성
        double randomValue = min.doubleValue() + (max.doubleValue() - min.doubleValue()) * random.nextDouble();

        // BigDecimal로 변환 후 scale 적용
        BigDecimal randomBigDecimal = new BigDecimal(randomValue).setScale(scale, RoundingMode.HALF_UP);

        return randomBigDecimal;
    }



}
