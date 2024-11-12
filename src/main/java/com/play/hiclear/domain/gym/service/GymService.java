package com.play.hiclear.domain.gym.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.dto.response.GeoCodeResponse;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.common.utils.DistanceCalculator;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.gym.dto.request.DistanceRequest;
import com.play.hiclear.domain.gym.dto.request.GymCreateRequest;
import com.play.hiclear.domain.gym.dto.request.GymUpdateRequest;
import com.play.hiclear.domain.gym.dto.response.GymCreateResponse;
import com.play.hiclear.domain.gym.dto.response.GymDetailResponse;
import com.play.hiclear.domain.gym.dto.response.GymSimpleResponse;
import com.play.hiclear.domain.gym.dto.response.GymUpdateResponse;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.schedule.dto.response.ClubScheduleResponse;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.schedule.repository.ScheduleRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GymService {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final GeoCodeService geoCodeService;
    private final ScheduleRepository scheduleRepository;

    /**
     * 체육관 생성
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
     * 사업자 본인소유 체육관 조회
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


    public GymDetailResponse get(Long gymId) {
        Gym gym = gymRepository.findByIdAndDeletedAtIsNullOrThrow(gymId);

        if (gym.getGymType() == GymType.PRIVATE) {
            return new GymDetailResponse(gym);
        } else { // public 인 경우 모임 리스트와 당날 스케줄 정보 가져오기
            // 공립 체육관의 모임 리스트 가져오기 (최근 한달간 4회 이상 모임일정을 개설한)
            LocalDateTime now = LocalDateTime.now();
            List<Club> clubs = scheduleRepository.findAllClubsByScheduleAtGym(now.minusDays(30), now, gym.getRegionAddress());
            List<String> clubNames = clubs.stream()
                    .map(Club::getClubname)
                    .collect(Collectors.toList());
            // 당날 스케줄 정보가져오기
            LocalDateTime dayStart = LocalDate.now().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);

            List<Schedule> schedules = scheduleRepository.findSchedulesByDayAndLocation(dayStart, dayEnd, gym.getRegionAddress());


            List<ClubScheduleResponse> todaySchedule = schedules.stream()
                    .map(ClubScheduleResponse::new)
                    .collect(Collectors.toList());

            return new GymDetailResponse(gym, clubNames, todaySchedule);
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