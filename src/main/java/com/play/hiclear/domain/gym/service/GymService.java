package com.play.hiclear.domain.gym.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.club.entity.Club;
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
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
     * 체육관 검색 v4
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
            AuthUser authUser, String name, String address, GymType gymType,
            int page, int size, Double requestDistance) {

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        Pageable pageable = PageRequest.of(page - 1, size);

        return gymRepository.search(name, address, gymType,
                user.getLocation(), requestDistance, pageable);
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


    public GymDetailResponse get(Long gymId) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Gym.class.getSimpleName()));

        if (gym.getGymType() == GymType.PRIVATE) {
            return new GymDetailResponse(gym);
        } else { // public 인 경우 모임 리스트와 당날 스케줄 정보 가져오기
            // 공립 체육관의 모임 리스트 가져오기 (최근 한달간 4회 이상 모임일정을 개설한)
            LocalDateTime now = LocalDateTime.now();
            List<Club> clubs = scheduleRepository.findAllClubsByScheduleAtGym(now.minusDays(30), now, gym.getRegionAddress());
            List<String> clubNames = clubs.stream()
                    .map(Club::getClubname)
                    .toList();

            // 당날 스케줄 정보가져오기 올때 필요한 당날 00:00~24:00 변수
            LocalDateTime dayStart = LocalDate.now().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);

            List<Schedule> schedules = scheduleRepository.findSchedulesByDayAndLocation(dayStart, dayEnd, gym.getRegionAddress());


            List<ClubScheduleResponse> todaySchedule = schedules.stream()
                    .map(ClubScheduleResponse::new)
                    .toList();

            return new GymDetailResponse(gym, clubNames, todaySchedule);
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