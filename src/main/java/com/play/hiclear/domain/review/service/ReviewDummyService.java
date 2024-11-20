package com.play.hiclear.domain.review.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateRequest;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.meeting.repository.MeetingRepository;
import com.play.hiclear.domain.review.entity.Review;
import com.play.hiclear.domain.review.enums.MannerRank;
import com.play.hiclear.domain.review.repository.ReviewRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ReviewDummyService {

    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ReviewRepository reviewRepository;


    public void generateDummyReviews() {

        // 유저 1
        String encodePassword1 = passwordEncoder.encode("A1234567*");
        Point userPoint1 = createPoint(126.977829174031, 37.5663174209601);
        userPoint1.setSRID(4326);
        User reviewer = new User("리뷰어", "adminuser1@gmail.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", userPoint1, encodePassword1, Ranks.RANK_A, UserRole.BUSINESS);
        userRepository.save(reviewer);

        // 유저 2
        String encodePassword2 = passwordEncoder.encode("A1234567*");
        Point userPoint2 = createPoint(126.977829174031, 37.5663174209601);
        userPoint2.setSRID(4326);
        User reviewee = new User("리뷰이", "adminuser2@gmail.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", userPoint2, encodePassword2, Ranks.RANK_A, UserRole.BUSINESS);
        userRepository.save(reviewee);

        // Meeting 생성
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        MeetingCreateRequest request = new MeetingCreateRequest("title", "서울 관악구 신림동 533-29", "content", startTime, endTime, Ranks.RANK_A, 12);
        GeoCodeDocument geoCodeDocument = new GeoCodeDocument(37.5665, 126.9780, "서울 관악구 신림동 533-29", "서울 관악구 조원로 89-1");
        Meeting meeting = new Meeting(request, reviewer, geoCodeDocument);
        meetingRepository.save(meeting);

        // MannerRank
        MannerRank [] mannerRanks = { MannerRank.LOW, MannerRank.HIGH, MannerRank.MEDIUM };
        Random random = new Random();

        // Ranks
        Ranks [] gradeRanks = { Ranks.RANK_A, Ranks.RANK_B, Ranks.RANK_C , Ranks.RANK_D , Ranks.RANK_E};


        // 10,000개의 리뷰 데이터를 생성
        for (int i = 0; i < 10000; i++) {
            // 리뷰 생성
            try {

                MannerRank randomMannerRank = mannerRanks[random.nextInt(mannerRanks.length)];
                Ranks randomGradeRank = gradeRanks[random.nextInt(gradeRanks.length)];
                Review review = new Review(reviewer, reviewee, meeting, randomMannerRank, randomGradeRank);
                reviewRepository.save(review);
            } catch (Exception e) {
                // 리뷰 생성에 실패할 경우 출력
                System.out.println("리뷰 생성 실패: " + e.getMessage());
            }

            // 배치로 1000개마다 flush (성능 최적화)
            if (i % 1000 == 0) {
                System.out.println("생성된 리뷰 수: " + i);
            }
        }

        System.out.println("리뷰 10,000건 생성 완료.");
    }

    private Point createPoint(Double longitude, Double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
    }

}