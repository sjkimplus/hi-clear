package com.play.hiclear.domain.gym.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.common.utils.DistanceCalculator;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GymService {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final GeoCodeService geoCodeService;
    private final DistanceCalculator distanceCalculator;

    /**
     * 체육관 생성
     *
     * @param authUser
     * @param request
     * @return GymCreateResponse
     */
    @Transactional
    public GymCreateResponse create(AuthUser authUser, GymCreateRequest request) {

        // 유저 확인
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        GeoCodeDocument geoCodeDocument = geoCodeService.getGeoCode(request.getAddress());


        Gym gym = new Gym(
                request.getName(),
                request.getDescription(),
                geoCodeDocument.getRegionAddress(),
                geoCodeDocument.getRoadAddress(),
                geoCodeDocument.getLatitude(),
                geoCodeDocument.getLongitude(),
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
     * 체육관 검색 기능(체육관명, 주소, 타입으로 조건가능)
     *
     * @param page
     * @param size
     * @param name
     * @param address
     * @param gymType
     * @return
     */
    public Page<GymSimpleResponse> search(int page, int size, String name, String address, GymType gymType) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Gym> gyms = gymRepository.searchGyms(name, address, gymType, pageable);

        return gyms.map(this::convertGymSimpleResponse);
    }


    /**
     * 가까운 체육관 조회
     *
     * @param authUser
     * @param page
     * @param size
     * @param requestDistance
     * @return Page<GymSimpleResponse>
     */
    public Page<GymSimpleResponse> searchNearby(AuthUser authUser, int page, int size, double requestDistance) {

        Pageable pageable = PageRequest.of(page - 1, size);

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        List<Gym> gyms = gymRepository.findAll();

        List<GymSimpleResponse> aroundGymsList = gyms.stream()
                .filter(gym -> Objects.isNull(gym.getDeletedAt()))
                .map(gym -> {
                    // 거리 계산
                    double distance = distanceCalculator.calculateDistance(
                            user.getLatitude(), user.getLongitude(),
                            gym.getLatitude(), gym.getLongitude()
                    ).setScale(1, RoundingMode.HALF_UP).doubleValue();

                    // 거리 조건 필터링
                    if (distance <= requestDistance) {
                        return new GymSimpleResponse(gym.getName(), gym.getRegionAddress(), distance);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(GymSimpleResponse::getDistance))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), aroundGymsList.size());
        List<GymSimpleResponse> subList = aroundGymsList.subList(start, end);

        return new PageImpl<>(subList, pageable, aroundGymsList.size());
    }

    /**
     * 사업자 본인소유 체육관 조회
     *
     * @param authUser
     * @param page
     * @param size
     * @return Page<GymSimpleResponse>
     */
    public Page<GymSimpleResponse> businessSearch(AuthUser authUser, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Gym> gyms = gymRepository.findByUserIdAndDeletedAtIsNull(authUser.getUserId(), pageable);

        return gyms.map(this::convertGymSimpleResponse);
    }

    /**
     * 체육관 정보 수정
     *
     * @param authUser
     * @param gymId
     * @param gymUpdateRequest
     * @return GymUpdateResponse
     */
    @Transactional
    public GymUpdateResponse update(AuthUser authUser, Long gymId, GymUpdateRequest gymUpdateRequest) {

        Gym gym = gymRepository.findByIdAndDeletedAtIsNullOrThrow(gymId);

        // 해당 체육관 사업주가 아닌경우 예외 발생
        checkBusinessAuth(gym.getUser(), authUser);

        // 주소 불러오기
        GeoCodeDocument geoCodeDocument = geoCodeService.getGeoCode(gymUpdateRequest.getUpdateAddress());

        gym.update(
                gymUpdateRequest.getUpdateName(),
                gymUpdateRequest.getUpdateDescription(),
                geoCodeDocument.getRegionAddress(),
                geoCodeDocument.getRoadAddress(),
                geoCodeDocument.getLatitude(),
                geoCodeDocument.getLongitude()
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
     * @param authUser
     * @param gymId
     */
    @Transactional
    public void delete(AuthUser authUser, Long gymId) {

        Gym gym = gymRepository.findByIdAndDeletedAtIsNullOrThrow(gymId);

        // 해당 체육관 사업주가 아닌경우 예외 발생
        checkBusinessAuth(gym.getUser(), authUser);

        gym.markDeleted();
    }


    public GymDetailResponse get(AuthUser authUser, Long gymId) {
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


    // GymSimpleResponse 객체 변환 메서드
    private GymSimpleResponse convertGymSimpleResponse(Gym gym) {
        return new GymSimpleResponse(
                gym.getName(),
                gym.getRegionAddress());
    }


    private void checkBusinessAuth(User ownUser, AuthUser requestUser) {
        if (!Objects.equals(ownUser.getId(), requestUser.getUserId())) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

    }


}