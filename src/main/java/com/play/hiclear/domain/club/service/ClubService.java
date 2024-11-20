package com.play.hiclear.domain.club.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.domain.club.dto.request.ClubCreateRequest;
import com.play.hiclear.domain.club.dto.request.ClubDeleteRequest;
import com.play.hiclear.domain.club.dto.request.ClubUpdateRequest;
import com.play.hiclear.domain.club.dto.response.ClubGetResponse;
import com.play.hiclear.domain.club.dto.response.ClubSearchResponse;
import com.play.hiclear.domain.club.dto.response.ClubUpdateResponse;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.entity.ClubDocument;
import com.play.hiclear.domain.club.repository.ClubElasticsearchRepository;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import com.play.hiclear.domain.clubmember.enums.ClubMemberRole;
import com.play.hiclear.domain.clubmember.repository.ClubMemberRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import com.play.hiclear.domain.club.entity.ClubDocument;
import com.play.hiclear.domain.club.repository.ClubElasticsearchRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final ClubElasticsearchRepository clubElasticsearchRepository;

    private final GeoCodeService geoCodeService;

    private final PasswordEncoder passwordEncoder;

    /**
     * @param userId            로그인한 유저의 Id
     * @param clubCreateRequest DTO
     */
    @Transactional
    public void create(Long userId, ClubCreateRequest clubCreateRequest) {

        //  유저 조회
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);

        // 주소값 가져오기
        GeoCodeDocument geoCodeDocument = geoCodeService.getGeoCode(clubCreateRequest.getAddress());

        Point location = geoCodeService.createPoint(geoCodeDocument);

        //  모임 생성
        Club club = clubRepository.save(
                Club.builder()
                        .clubname(clubCreateRequest.getClubname())
                        .clubSize(clubCreateRequest.getClubSize())
                        .intro(clubCreateRequest.getIntro())
                        .regionAddress(geoCodeDocument.getRegionAddress())
                        .roadAddress(geoCodeDocument.getRoadAddress())
                        .location(location)
                        .password(passwordEncoder.encode(clubCreateRequest.getPassword()))
                        .owner(user)
                        .build()
        );

        clubElasticsearchRepository.save(
                ClubDocument.builder()
                        .id(club.getId())
                        .clubname(club.getClubname())
                        .intro(club.getIntro())
                        .regionAddress(club.getRegionAddress())
                        .roadAddress(club.getRoadAddress())
                        .build()
        );

        //  모임 개설자를 모임장으로 클럽멤버 추가
        clubMemberRepository.save(
                ClubMember.builder()
                        .user(user)
                        .club(club)
                        .clubMemberRole(ClubMemberRole.ROLE_MASTER)
                        .build()
        );
    }

    /**
     * @param clubId 조회하려는 clubId
     * @return 모임 이름, 모임 정원, 모임 소개글, 모임 지역, 회원들
     */
    public ClubGetResponse get(Long clubId) {

        //  모임 유효성 검사
        Club club = clubRepository.findByIdAndDeletedAtIsNullOrThrow(clubId);

        return new ClubGetResponse(club);
    }

    /**
     * @param userId            현재 로그인된 userId
     * @param clubId            변경할 클럽의 clubId
     * @param clubUpdateRequest 변경할 내용을 담은 DTO
     * @return 모임 이름, 모임 정원, 모임 소개글, 모임 지역
     */
    @Transactional
    public ClubUpdateResponse update(Long userId, Long clubId, ClubUpdateRequest clubUpdateRequest) {

        //  유저 조회
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);
        //  모임 조회
        Club club = clubRepository.findByIdAndDeletedAtIsNullOrThrow(clubId);
        //  현재 로그인된 유저의 모임멤버 조회
        ClubMember clubMember = clubMemberRepository.findByUserIdAndClubIdOrThrow(user.getId(), club.getId());

        //  모임 권한 확인
        checkClubAdmin(clubMember);
        //  비밀번호 확인
        checkPassword(clubUpdateRequest.getPassword(), club.getPassword());

        club.updateClub(clubUpdateRequest);

        return new ClubUpdateResponse(club);
    }

    /**
     * @return 모임 이름, 모임 소개글
     */
    public Page<ClubSearchResponse> search(int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("modifiedAt").descending());

        Page<Club> clubList = clubRepository.findAll(pageable);

        return clubList.map(ClubSearchResponse::new);
    }

    @Transactional
    public void delete(Long userId, Long clubId, ClubDeleteRequest clubDeleteRequest) {

        //  유저 조회
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);
        //  모임 조회
        Club club = clubRepository.findByIdAndDeletedAtIsNullOrThrow(clubId);
        //  모임 멤버 조회
        ClubMember clubMember = clubMemberRepository.findByUserIdAndClubIdOrThrow(user.getId(), club.getId());

        //  모임 권한 확인
        checkClubAdmin(clubMember);
        //  비밀번호 확인
        checkPassword(clubDeleteRequest.getPassword(), club.getPassword());

        club.markDeleted();
    }

    // 비밀번호 확인
    private void checkPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST_PASSWORD);
        }
    }

    // 모임장 권한 확인
    private void checkClubAdmin(ClubMember clubMember) {
        if (clubMember.getClubMemberRole() != ClubMemberRole.ROLE_MASTER) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Club.class.getSimpleName());
        }
    }

    public Page<ClubDocument> elsearch(
            int page, int size,
            String clubname, String intro,
            String regionAddress, String roadAddress) {

        Pageable pageable = PageRequest.of(page - 1, size);

        boolean addressCondition = (regionAddress != null && !regionAddress.isEmpty())
                || (roadAddress != null && !roadAddress.isEmpty());

        boolean clubnameCondition = clubname != null && !clubname.isEmpty();

        boolean introCondition = intro != null && !intro.isEmpty();

        if (clubnameCondition && addressCondition && introCondition) {
            return clubElasticsearchRepository.findByClubnameContainingAndIntroContainingAndRegionAddressContainingAndRoadAddressContaining(clubname, intro, regionAddress, roadAddress, pageable);
        }

        if (introCondition) {
            return clubElasticsearchRepository.findByIntroContaining(intro, pageable);
        }

        if (clubnameCondition) {
            return clubElasticsearchRepository.findByClubnameContaining(clubname, pageable);
        }

        if (addressCondition) {
            return clubElasticsearchRepository.findByRegionAddressContainingAndRoadAddressContaining(regionAddress, roadAddress, pageable);
        }

        return clubElasticsearchRepository.findAll(pageable);
    }

    @Transactional
    public void createDummy() {

        if (clubRepository.count() > 0) {
            throw new CustomException(ErrorCode.DUMMY_ALREADY_EXIST);
        }

        Random random = new Random();
        String encodePassword = passwordEncoder.encode("A1234567*");
        Point userPoint = createPoint(126.977829174031, 37.5663174209601);
        User user = new User("이름", "adminuser21@gmail.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", userPoint, encodePassword, Ranks.RANK_A, UserRole.BUSINESS);
        userPoint.setSRID(4326);
        userRepository.save(user);
        String[] clubnameL = {" 클럽", " 모임", " 동호회"};
        String[] clubnameM = {" 초보", " 초심", " 왕초보", " 고수", " 즐거운", " 화목한"};
        String[] regions = {
                "서울", "부산", "대구", "인천", "광주",
                "대전", "울산", "수원", "성남", "고양",
                "용인", "청주", "전주", "포항", "창원"
        };

        for (int i = 0; i < 100; i++) {
            Integer clubSize = 5;
            String clubname = regions[random.nextInt(regions.length)]
                    + clubnameM[random.nextInt(clubnameM.length)]
                    + clubnameL[random.nextInt(clubnameL.length)];

            String intro = regions[random.nextInt(regions.length)]
                    + clubnameM[random.nextInt(clubnameM.length)]
                    + clubnameL[random.nextInt(clubnameL.length)];
            String regionAddress = regions[random.nextInt(regions.length)];
            String password = "A1234567*";
            Club club = new Club(user, clubname, clubSize, intro, regionAddress, userPoint, regionAddress, password);
            clubRepository.save(club);
            clubElasticsearchRepository.save(
                    ClubDocument.builder()
                            .id(club.getId())
                            .clubname(club.getClubname())
                            .intro(club.getIntro())
                            .regionAddress(club.getRegionAddress())
                            .roadAddress(club.getRoadAddress())
                            .build());
        }
    }

    private Point createPoint(Double longitude, Double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
    }
}
