package com.play.hiclear.domain.gym.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@RequiredArgsConstructor
public class GymDummyDataService {

    private final GymRepository gymRepository;
    private final UserRepository userRepository;

    @PostConstruct
    @Transactional
    public void generateDummyData() {

        // 데이터베이스에 더미 데이터가 이미 존재하면 생성하지 않음
        if (gymRepository.count() > 0) {
            System.out.println("더미 데이터가 이미 존재합니다.");
            return;
        }

        //더미 데이터 생성
        User user = new User("이름", "adjwws@anasdaw.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        userRepository.save(user);

        String csvFile = "src/main/resources/data/address_dummy.csv";
        int i = 0;

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(csvFile)))) {
            String[] nextLine;
            reader.readNext();
            while ((nextLine = reader.readNext()) != null && i < 22000) {
                // CSV 파일의 각 열을 Gym 객체의 필드에 맞게 매핑
                String name = nextLine[0] + nextLine[1] + nextLine[2];
                String regionAddress = nextLine[0] + " " + nextLine[1] + " " + nextLine[2];
                Double latitude = Double.parseDouble(nextLine[3]);
                Double longitude = Double.parseDouble(nextLine[4]);

                // Gym 객체 생성 후 저장
                Gym gym = new Gym(name, null, regionAddress, null, latitude, longitude, GymType.PUBLIC, user);
                gymRepository.save(gym);

                if (i % 100 == 0) {
                    gymRepository.flush();
                }

                i++;
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
