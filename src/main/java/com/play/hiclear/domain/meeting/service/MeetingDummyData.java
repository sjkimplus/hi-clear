package com.play.hiclear.domain.meeting.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateRequest;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.meeting.entity.MeetingDocument;
import com.play.hiclear.domain.meeting.repository.MeetingElasticSearchRepository;
import com.play.hiclear.domain.meeting.repository.MeetingRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeetingDummyData {

    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingElasticSearchRepository meetingESRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Random random = new Random();

    @Transactional
    public void generateDummyMeetings() {

        // 사용자 생성
        String encodePassword = passwordEncoder.encode("A1234567*");
        Point userPoint = createPoint(126.977829174031, 37.5663174209601);
        userPoint.setSRID(4326);
        User user = new User("이름", "adminuser12345@gmail.com",
                "서울 중구 태평로1가 31", "서울 중구 세종대로 110",
                userPoint, encodePassword, Ranks.RANK_A, UserRole.BUSINESS);
        userRepository.save(user);

        // Meeting 생성
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);

        // Generate 10,000 unique titles
        for (int i = 1; i <= 1000; i++) {
            String uniqueTitle = generateUniqueTitle(i);

            MeetingCreateRequest request = new MeetingCreateRequest(
                    uniqueTitle, "서울 관악구 신림동 533-29", "content", startTime, endTime, Ranks.RANK_A, 12);

            GeoCodeDocument geoCodeDocument = new GeoCodeDocument(
                    37.5665, 126.9780, "서울 관악구 신림동 533-29", "서울 관악구 조원로 89-1");

            Meeting meeting = new Meeting(request, user, geoCodeDocument);
            meetingRepository.save(meeting);
            Meeting foundMeeting = meetingRepository.findByIdAndDeletedAtIsNullOrThrow(meeting.getId());
            MeetingDocument meetingDocument = new MeetingDocument(foundMeeting);
            meetingESRepository.save(meetingDocument);

            // Flush every 2500 iterations
            if (i % 250 == 0) {
                meetingRepository.flush();
            }
        }
    }

    private String generateUniqueTitle(int index) {
        String[] themes = {"급벙", "번개", "모임", "초대"};
        String[] locations = {"삼성동", "미성체육관", "국사봉", "계담체육관", "서울역", "광화문", "잠실", "신림동", "관악구", "마포구"};
        String[] extras = {"즐거운", "초심", "운동후 식사 O", "20대 혼복 모집"};

        // Generate title using index to ensure uniqueness
        String theme = themes[random.nextInt(themes.length)];
        String location = locations[random.nextInt(locations.length)];
        String extra = extras[random.nextInt(extras.length)];
        return String.format("%s %s %s (%d)", location, theme, extra, index);
    }

    private Point createPoint(Double longitude, Double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
    }
}

