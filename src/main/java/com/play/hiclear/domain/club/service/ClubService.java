package com.play.hiclear.domain.club.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.club.dto.request.ClubCreateRequest;
import com.play.hiclear.domain.club.dto.request.ClubDeleteRequest;
import com.play.hiclear.domain.club.dto.request.ClubUpdateRequest;
import com.play.hiclear.domain.club.dto.response.ClubGetResponse;
import com.play.hiclear.domain.club.dto.response.ClubNearResponse;
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
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Page<ClubSearchResponse> search(int page, int size, String clubname, String intro, String regionAddress, String roadAddress) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Club> clubList = clubRepository.findByDeletedAtIsNullAndFilters(clubname, intro, regionAddress, roadAddress, pageable);

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
        // 이미 데이터가 존재하는 경우 예외 처리
        if (clubRepository.count() > 0) {
            throw new CustomException(ErrorCode.DUMMY_ALREADY_EXIST);
        }

        // 비밀번호와 기본 사용자 생성
        String encodedPassword = passwordEncoder.encode("A1234567*");
        Point userPoint = createPoint(126.977829174031, 37.5663174209601);
        userPoint.setSRID(4326);

        User adminUser = createUser(
                "이름",
                "adminuser21@gmail.com",
                "서울 중구 태평로1가 31",
                "서울 중구 세종대로 110",
                userPoint,
                encodedPassword,
                Ranks.RANK_A,
                UserRole.BUSINESS
        );

        // 클럽 이름과 관련 데이터
        String[] clubSuffixes = {" 클럽", " 모임", " 동호회"};
        String[] clubDescriptors = {" 초보", " 초심", " 왕초보", " 고수", " 즐거운", " 화목한"};
        String[] regions = {
                "서울", "부산", "대구", "인천", "광주",
                "대전", "울산", "수원", "성남", "고양",
                "용인", "청주", "전주", "포항", "창원"
        };

        // 더미 클럽 생성
        Random random = new Random();
//        List<Club> clubs = new ArrayList<>();
//        List<ClubDocument> clubDocuments = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            String clubName = generateRandomName(random, regions, clubDescriptors, clubSuffixes);
            String intro = generateRandomName(random, regions, clubDescriptors, clubSuffixes);
            String regionAddress = regions[random.nextInt(regions.length)];

            Club club = new Club(
                    adminUser,
                    clubName,
                    5, // clubSize
                    intro,
                    regionAddress,
                    userPoint,
                    regionAddress,
                    "A1234567*" // 비밀번호
            );
            clubRepository.save(club);

            clubElasticsearchRepository.save(ClubDocument.builder()
                    .id(club.getId())
                    .clubname(clubName)
                    .intro(intro)
                    .regionAddress(regionAddress)
                    .roadAddress(regionAddress)
                    .build());

            if (i % 2500 == 0) {
                clubRepository.flush();
            }
        }
    }

    private User createUser(String name, String email, String address, String roadAddress, Point point,
                            String password, Ranks rank, UserRole role) {
        User user = new User(name, email, address, roadAddress, point, password, rank, role);
        userRepository.save(user);
        return user;
    }

    private String generateRandomName(Random random, String[] regions, String[] descriptors, String[] suffixes) {
        return regions[random.nextInt(regions.length)] +
                descriptors[random.nextInt(descriptors.length)] +
                suffixes[random.nextInt(suffixes.length)];
    }

    private Point createPoint(Double longitude, Double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
    }

    public Page<ClubNearResponse> near(AuthUser authUser, Double distance, int page, int size) {

        if (distance != null && distance != 5 && distance != 10 && distance != 50 && distance != 100) {
            throw new CustomException(ErrorCode.INVALID_DISTANCE);
        }

        if (distance == null) {
            distance = 1000d;
        }

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        Pageable pageable = PageRequest.of(page - 1, size);

        return clubRepository.search(user.getLocation(), distance, pageable);
    }
}
