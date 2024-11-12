package com.play.hiclear.domain.gym.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.gym.dto.request.GymCreateRequest;
import com.play.hiclear.domain.gym.dto.request.GymUpdateRequest;
import com.play.hiclear.domain.gym.dto.response.GymCreateResponse;
import com.play.hiclear.domain.gym.dto.response.GymDetailResponse;
import com.play.hiclear.domain.gym.dto.response.GymSimpleResponse;
import com.play.hiclear.domain.gym.dto.response.GymUpdateResponse;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GymService {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final GeoCodeService geoCodeService;

    /**
     * 체육관 생성
     *
     * @param authUser 인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param request  체육관 생성 정보를 포함한 객체
     * @return 생성된 체육관의 ID를 제외한 모든 정보를 표시
     */
    @Transactional
    public GymCreateResponse create(AuthUser authUser, GymCreateRequest request) {

        // 유저 확인
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        GeoCodeDocument geoCodeDocument = geoCodeService.getGeoCode(request.getAddress());

        Point location = geoCodeService.createPoint(geoCodeDocument);

        Gym gym = new Gym(
                request.getName(),
                request.getDescription(),
                geoCodeDocument.getRegionAddress(),
                geoCodeDocument.getRoadAddress(),
                location,
                GymType.of(request.getGymType()),
                user
        );

        gymRepository.save(gym);

        return new GymCreateResponse(
                gym.getId(),
                gym.getName(),
                gym.getDescription(),
                gym.getRegionAddress(),
                gym.getRoadAddress(),
                gym.getGymType()
        );
    }

    /**
     * 체육관 검색 v2
     *
     * @param authUser        인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param name            이름(조건검색)
     * @param address         주소(조건검색)
     * @param gymType         타입(조건검색)
     * @param page            페이징의 페이지
     * @param size            페이지당 표시 개수
     * @param requestDistance 거리(조건검색)
     * @return 조건에 부합하는 체육관(이름, 주소, 거리)들 반환
     */
    public Page<GymSimpleResponse> search(
            AuthUser authUser, String name, String address,
            GymType gymType, int page, int size, Double requestDistance) {

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        Point userLocation = user.getLocation();

        Pageable pageable = PageRequest.of(page - 1, size);

        return gymRepository.search(name, address, gymType, userLocation, requestDistance, pageable);
    }


    public Page<GymSimpleResponse> searchv3(
            AuthUser authUser, String name, String address, GymType gymType,
            int page, int size, Double requestDistance) {

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        Double userLatitude = user.getLocation().getY();
        Double userLongitude = user.getLocation().getX();

        // 위도 1도는 약 113.32km, 경도 1도는 약 113.32 * cos(위도)
        Double minLat = new BigDecimal(userLatitude - requestDistance / 111.32).setScale(6, RoundingMode.DOWN).doubleValue();
        Double maxLat = new BigDecimal(userLatitude + requestDistance / 111.32).setScale(6, RoundingMode.UP).doubleValue();
        Double minLon = new BigDecimal(userLongitude - requestDistance / (111.32 * Math.cos(Math.toRadians(userLatitude)))).setScale(6, RoundingMode.DOWN).doubleValue();
        Double maxLon = new BigDecimal(userLongitude + requestDistance / (111.32 * Math.cos(Math.toRadians(userLatitude)))).setScale(6, RoundingMode.UP).doubleValue();

        System.out.printf("%s, %s, %s, %s%n", minLat, maxLat, minLon, maxLon);

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Gym> gyms = gymRepository.searchv3(name, address, gymType,
                user.getLocation(), minLat, maxLat, minLon, maxLon, requestDistance, pageable);

        return null;
    }


    /**
     * 사업자 소유 체육관 조회
     *
     * @param authUser 인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param page     페이징의 페이지
     * @param size     페이지당 표시 개수
     * @return 체육관의 이름과 주소를 반환
     */
    public Page<GymSimpleResponse> businessSearch(AuthUser authUser, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Gym> gyms = gymRepository.findByUserIdAndDeletedAtIsNull(authUser.getUserId(), pageable);

        return gyms.map(this::convertGymSimpleResponse);
    }

    /**
     * 체육관 정보 수정
     *
     * @param authUser         인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param gymId            수정할 체육관의 ID
     * @param gymUpdateRequest 체육관의 수정 할 정보를 포함한 객체
     * @return 수정된 체육관의 정보(이름, 설명, 주소) 반환
     */
    @Transactional
    public GymUpdateResponse update(AuthUser authUser, Long gymId, GymUpdateRequest gymUpdateRequest) {

        Gym gym = gymRepository.findByIdAndDeletedAtIsNullOrThrow(gymId);

        // 해당 체육관 사업주가 아닌경우 예외 발생
        checkBusinessAuth(gym.getUser(), authUser);

        // 주소 불러오기
        GeoCodeDocument geoCodeDocument = geoCodeService.getGeoCode(gymUpdateRequest.getUpdateAddress());

        Point location = geoCodeService.createPoint(geoCodeDocument);

        gym.update(
                gymUpdateRequest.getUpdateName(),
                gymUpdateRequest.getUpdateDescription(),
                geoCodeDocument.getRegionAddress(),
                geoCodeDocument.getRoadAddress(),
                location
        );

        return new GymUpdateResponse(
                gym.getName(),
                gym.getDescription(),
                gym.getRegionAddress()
        );
    }


    /**
     * 체육관 삭제(Soft)
     *
     * @param authUser 인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param gymId    삭제할 체육관의 ID
     */
    @Transactional
    public void delete(AuthUser authUser, Long gymId) {

        Gym gym = gymRepository.findByIdAndDeletedAtIsNullOrThrow(gymId);

        // 해당 체육관 사업주가 아닌경우 예외 발생
        checkBusinessAuth(gym.getUser(), authUser);

        gym.markDeleted();
    }


    public GymDetailResponse get(AuthUser authUser, Long gymId) {

        userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Gym.class.getSimpleName()));

        if (gym.getGymType() == GymType.PRIVATE) {
            return new GymDetailResponse(gym);
        } else { // public 인 경우 모임 리스트와 당날 스케줄 정보 가져오기
            // 공립 체육관의 모임 리스트 가져오기


            // 당날 스케줄 정보가져오기

            return new GymDetailResponse(gym);
        }
    }


    // GymSimpleResponse(거리X) 객체 변환 메서드
    private GymSimpleResponse convertGymSimpleResponse(Gym gym) {
        return new GymSimpleResponse(
                gym.getName(),
                gym.getRegionAddress());
    }


    // 사업자 권한 확인
    private void checkBusinessAuth(User ownUser, AuthUser requestUser) {
        if (!Objects.equals(ownUser.getId(), requestUser.getUserId())) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

    }

}