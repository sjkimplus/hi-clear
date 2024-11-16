package com.play.hiclear.domain.schedule.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.notification.enums.NotiType;
import com.play.hiclear.domain.notification.service.NotiService;
import com.play.hiclear.domain.schduleparticipant.entity.ScheduleParticipant;
import com.play.hiclear.domain.schduleparticipant.repository.ScheduleParticipantRepository;
import com.play.hiclear.domain.schedule.dto.request.ScheduleRequest;
import com.play.hiclear.domain.schedule.dto.request.ScheduleUpdateRequest;
import com.play.hiclear.domain.schedule.dto.response.ScheduleSearchDetailResponse;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.schedule.repository.ScheduleRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleParticipantRepository scheduleParticipantRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final GeoCodeService geoCodeService;
    private final NotiService notiService;

    /**
     * 모임 일정 생성
     * @param authUser
     * @param clubId
     * @param scheduleRequest
     * @return ScheduleSearchDetailResponse
     */
    @Transactional
    public ScheduleSearchDetailResponse create(AuthUser authUser, Long clubId, ScheduleRequest scheduleRequest) {
        log.info("모임 일정 생성 요청 - 사용자: {}, 클럽 ID: {}", authUser.getEmail(), clubId);

        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());
        Club club = clubRepository.findByIdAndDeletedAtIsNullOrThrow(clubId);

        // 클럽 멤버 확인
        validateClubMembership(user, club);

        // 참가자 유효성 검사 및 중복 확인
        Set<Long> participantIds = validateAndGetParticipants(scheduleRequest.getParticipants(), club, user.getId());

        // 유니크 제약 조건 체크: 동일한 클럽 내에서 동일한 제목과 시작 시간이 있는지 확인
        checkDuplicateSchedule(clubId, scheduleRequest.getStartTime(), scheduleRequest.getTitle());

        // 시간 검증 (create용 검증)
        validateCreateScheduleTimes(scheduleRequest.getStartTime(), scheduleRequest.getEndTime());

        // 위치 정보 가져오기 (GeoCodeService 호출)
        GeoCodeDocument geoCodeAddress = getGeoCodeInfo(scheduleRequest.getAddress());

        // 도로명 주소나 지번 주소가 없으면 예외 처리
        if (geoCodeAddress.getRegionAddress() == null && geoCodeAddress.getRoadAddress() == null) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Schedule.class.getSimpleName());
        }

        Schedule schedule = new Schedule(user, club, scheduleRequest.getTitle(), scheduleRequest.getDescription(), scheduleRequest.getStartTime(), scheduleRequest.getEndTime(),
                geoCodeAddress.getRegionAddress(), geoCodeAddress.getRoadAddress(), geoCodeAddress.getLatitude(), geoCodeAddress.getLongitude());

        Schedule savedSchedule = scheduleRepository.save(schedule);

        // 참가자 목록 추가
        addParticipants(savedSchedule, participantIds, club);

        log.info("모임 일정 생성 완료 - 일정 ID: {}, 제목: {}", savedSchedule.getId(), savedSchedule.getTitle());
        // 반환 DTO 생성
        return ScheduleSearchDetailResponse.from(savedSchedule, user.getEmail(), user.getId(), List.copyOf(participantIds));
    }

    /**
     * 모임 일정 단건 조회
     * @param scheduleId
     * @param authUser
     * @return ScheduleSearchDetailResponse
     */
    public ScheduleSearchDetailResponse get(Long scheduleId, AuthUser authUser) {
        log.info("모임 일정 조회 요청 - 일정 ID: {}, 사용자: {}", scheduleId, authUser.getEmail());

        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());
        // 일정 조회
        Schedule schedule = scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(scheduleId);

        // 일정 생성자와 참가자 목록 확인
        User creator = schedule.getUser();
        List<ScheduleParticipant> participants = scheduleParticipantRepository.findBySchedule(schedule);

        // 해당 모임의 멤버인지 확인
        validateClubMembership(user, schedule.getClub());

        log.info("모임 일정 조회 완료 - 일정 ID: {}, 제목: {}", schedule.getId(), schedule.getTitle());

        // 반환 DTO 생성
        return ScheduleSearchDetailResponse.from(schedule, authUser.getEmail(), creator.getId(),
                participants.stream().map(participant -> participant.getUser().getId()).toList());
    }

    /**
     * 클럽의 모임 일정 목록 조회
     * @param clubId
     * @param authUser
     * @param page
     * @param size
     * @param title
     * @param description
     * @param regionAddress
     * @param startDate
     * @param endDate
     * @return Page<Schedule>
     */
    public Page<Schedule> search(Long clubId, AuthUser authUser, int page, int size, String title, String description, String regionAddress, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("클럽의 모임 일정 목록 조회 요청 - 사용자: {}, 클럽 ID: {}", authUser.getEmail(), clubId);

        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());
        Club club = clubRepository.findByIdAndDeletedAtIsNullOrThrow(clubId);

        // 해당 모임의 멤버인지 확인
        validateClubMembership(user, club);

        Pageable pageable = PageRequest.of(page - 1, size);

        // 클럽의 삭제되지 않은 일정 목록을 필터링하여 조회
        Page<Schedule> schedules = scheduleRepository.findAllByClubAndDeletedAtIsNullAndFilters(
                club, title, description, regionAddress, startDate, endDate, pageable);

        log.info("모임 일정 목록 조회 완료 - 클럽 ID: {}, 조회된 일정 수: {}", clubId, schedules.getTotalElements());
        return schedules;
    }

    /**
     * 모임 일정 수정
     * @param scheduleId
     * @param scheduleUpdateRequest
     * @param authUser
     * @return ScheduleSearchDetailResponse
     */
    @Transactional
    public ScheduleSearchDetailResponse update(Long scheduleId, ScheduleUpdateRequest scheduleUpdateRequest, AuthUser authUser) {
        log.info("모임 일정 수정 요청 - 일정 ID: {}, 사용자: {}", scheduleId, authUser.getEmail());

        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());
        Schedule schedule = scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(scheduleId);

        // 생성자 권한 확인
        checkScheduleAuthority(schedule, user);

        // 클럽 ID 가져오기
        Long clubId = schedule.getClub().getId();

        // 유니크 제약 조건 체크 메서드 호출
        checkDuplicateSchedule(clubId, scheduleUpdateRequest.getStartTime(), scheduleUpdateRequest.getTitle());

        // 시간 검증 (update용 검증)
        validateUpdateScheduleTimes(schedule.getStartTime(), schedule.getEndTime(), scheduleUpdateRequest.getStartTime(), scheduleUpdateRequest.getEndTime());

        // 수정된 값만 업데이트
        schedule.updateSchedule(scheduleUpdateRequest);

        // 지역 정보(region)가 제공되면, 해당 정보를 사용하여 위치 정보를 갱신
        if (scheduleUpdateRequest.getAddress() != null && !scheduleUpdateRequest.getAddress().isEmpty()) {
            GeoCodeDocument geoCodeAddress = geoCodeService.getGeoCode(scheduleUpdateRequest.getAddress());

            // 도로명 주소 또는 지번 주소가 존재하면 위치 정보 갱신
            if (geoCodeAddress.getRegionAddress() != null || geoCodeAddress.getRoadAddress() != null) {
                schedule.updateLocation(geoCodeAddress.getRegionAddress(), geoCodeAddress.getRoadAddress(), geoCodeAddress.getLatitude(), geoCodeAddress.getLongitude());
            }
        }

        // 수정된 일정 저장
        Schedule updatedSchedule = scheduleRepository.save(schedule);

        log.info("모임 일정 수정 완료 - 일정 ID: {}, 제목: {}", updatedSchedule.getId(), updatedSchedule.getTitle());

        // 수정된 일정 반환 DTO 생성
        return ScheduleSearchDetailResponse.from(
                updatedSchedule,
                authUser.getEmail(),
                updatedSchedule.getUser().getId(),
                updatedSchedule.getScheduleParticipants().stream()
                        .map(participant -> participant.getUser().getId())
                        .toList()
        );
    }

    /**
     * 모임 일정 삭제
     * @param scheduleId
     * @param authUser
     */
    @Transactional
    public void delete(Long scheduleId, AuthUser authUser) {
        log.info("모임 일정 삭제 요청 - 일정 ID: {}, 사용자: {}", scheduleId, authUser.getEmail());

        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());
        // 일정 조회
        Schedule schedule = scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(scheduleId);

        // 사용자가 일정의 생성자인지 확인
        checkScheduleAuthority(schedule, user);

        // 참가자 삭제
        List<ScheduleParticipant> participants = scheduleParticipantRepository.findBySchedule(schedule);
        for (ScheduleParticipant participant : participants) {
            scheduleParticipantRepository.delete(participant);
        }

        log.info("모임 일정 삭제 완료 - 일정 ID: {}", scheduleId);
        // 일정 삭제
        schedule.markDeleted();
    }


    /**
     * 모임 일정에 참가자 추가
     * @param scheduleId
     * @param participantId
     * @param authUser
     */
    @Transactional
    public void addParticipant(Long scheduleId, Long participantId, AuthUser authUser) {
        log.info("모임 일정 참가자 추가 요청 - 사용자: {}, 일정 ID: {}, 참가자 ID: {}", authUser.getEmail(), scheduleId, participantId);
        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());
        Schedule schedule = scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(scheduleId);

        // 사용자가 일정의 생성자인지 확인
        checkScheduleAuthority(schedule, user);

        // 4. 참가자 조회 (참가할 사용자를 찾음)
        User participantUser = userRepository.findByIdAndDeletedAtIsNullOrThrow(participantId);

        // 5. 해당 모임의 멤버인지 확인
        validateClubMembership(participantUser, schedule.getClub());

        // 6. 이미 해당 모임 일정에 참가한 사용자라면 예외 발생
        scheduleParticipantRepository.checkIfAlreadyParticipating(schedule, participantUser);

        // 7. 참가자 추가
        ScheduleParticipant additionalParticipant = new ScheduleParticipant(schedule, participantUser, schedule.getClub());
        scheduleParticipantRepository.save(additionalParticipant);

        schedule.getScheduleParticipants()
                .stream().filter(paticiapnt -> !(paticiapnt.getUser().equals(participantUser) || paticiapnt.getUser().equals(user)))
                .forEach(participant -> notiService.sendNotification(
                        participant.getUser(),
                        NotiType.SCHEDULE,
                        String.format("%s 님이 %s 일정에 참가했습니다", participantUser.getName(), schedule.getTitle()),
                        String.format("/v1/schedules/%d/participants/%d" , schedule.getId(), participantUser.getId()
                        )));

        log.info("모임 일정 참가자 추가 완료 - 일정 ID: {}, 참가자 ID: {}", scheduleId, participantId);
    }


    /**
     * 모임 일정에 참가자 삭제
     * @param scheduleId
     * @param participantId
     * @param authUser
     */
    @Transactional
    public void deleteParticipant(Long scheduleId, Long participantId, AuthUser authUser) {
        log.info("모임 일정 참가자 삭제 요청 - 사용자: {}, 일정 ID: {}, 참가자 ID: {}", authUser.getEmail(), scheduleId, participantId);
        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());

        Schedule schedule = scheduleRepository.findByIdAndDeletedAtIsNullOrThrow(scheduleId);

        // 사용자가 일정의 생성자인지 확인
        checkScheduleAuthority(schedule, user);

        // 삭제할 참가자 조회
        User participantUser = userRepository.findByIdAndDeletedAtIsNullOrThrow(participantId);

        // 해당 모임의 멤버인지 확인
        validateClubMembership(participantUser, schedule.getClub());

        // 이미 참가자인지 확인
        ScheduleParticipant participant = scheduleParticipantRepository.findByScheduleAndUserOrThrow(schedule, participantUser);
        scheduleParticipantRepository.delete(participant);

        log.info("모임 일정 참가자 삭제 완료 - 일정 ID: {}, 참가자 ID: {}", scheduleId, participantId);
    }

    // Schedule의 권한 확인
    private void checkScheduleAuthority(Schedule schedule, User user) {
        if (!schedule.getUser().getEmail().equals(user.getEmail())) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Schedule.class.getSimpleName());
        }
    }
    // 클럽 멤버 확인
    private void validateClubMembership(User user, Club club) {
        boolean isMember = club.getClubMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()));
        if (!isMember) {
            throw new CustomException(ErrorCode.SCHEDULE_NOT_A_CLUB_MEMBER);
        }
    }

    // 위치 정보를 가져오는 메서드
    private GeoCodeDocument getGeoCodeInfo(String region) {
        return geoCodeService.getGeoCode(region);
    }

    // 유니크 제약 조건 체크: 동일한 클럽 내에서 동일한 제목과 시작 시간이 있는지 확인
    private void checkDuplicateSchedule(Long clubId, LocalDateTime startTime, String title) {
        if (scheduleRepository.existsByClubIdAndStartTimeAndTitleAndDeletedAtIsNull(clubId, startTime, title)) {
            throw new CustomException(ErrorCode.DUPLICATE_SCHEDULE);
        }
    }

    // 예약 생성(시간 확인)
    private void validateCreateScheduleTimes(LocalDateTime startTime, LocalDateTime endTime) {
        // 시작 시간 검증
        if (startTime != null && startTime.isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME, "Start time cannot be in the past.");
        }

        // 종료 시간 검증
        if (endTime != null) {
            if (endTime.isBefore(LocalDateTime.now())) {
                throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME, "End time cannot be in the past.");
            }

            if (startTime != null && endTime.isBefore(startTime)) {
                throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME, "End time must be after start time.");
            }
        }
    }

    // 예약 수정(시간 확인)
    private void validateUpdateScheduleTimes(LocalDateTime originalStartTime, LocalDateTime originalEndTime, LocalDateTime updatedStartTime, LocalDateTime updatedEndTime) {
        // 1. startTime만 들어올 때
        if (updatedStartTime != null && updatedEndTime == null) {
            // startTime이 현재 시간보다 이전인 경우 에러
            if (updatedStartTime.isBefore(LocalDateTime.now())) {
                throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME);
            }


            // startTime이 기존의 endTime보다 이후인 경우 에러
            if (updatedStartTime.isAfter(originalEndTime)) {
                throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME);
            }
        }

        // 2. endTime만 들어올 때
        if (updatedEndTime != null && updatedStartTime == null) {
            // endTime이 현재 시간보다 이전인 경우 에러
            if (updatedEndTime.isBefore(LocalDateTime.now())) {
                throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME);
            }

            // endTime이 기존의 startTime보다 이전인 경우 에러
            if (updatedEndTime.isBefore(originalStartTime)) {
                throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME);
            }
        }

        // 3. startTime과 endTime 둘 다 들어올 때
        if (updatedStartTime != null && updatedEndTime != null) {
            // startTime과 endTime이 둘 다 현재 시간보다 이전인 경우 에러
            if (updatedStartTime.isBefore(LocalDateTime.now()) || updatedEndTime.isBefore(LocalDateTime.now())) {
                throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME);
            }

            // startTime이 endTime보다 이전인 경우가 아니라면 에러
            if (updatedStartTime.isAfter(updatedEndTime)) {
                throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME);
            }
        }
    }

    // 사용자가 Club의 회원인지 확인하고 유효한 참가자 ID를 반환
    private Set<Long> validateAndGetParticipants(List<Long> participantIds, Club club, Long currentUserId) {
        Set<Long> validParticipantIds = new HashSet<>();
        validParticipantIds.add(currentUserId);

        if (participantIds != null) {
            for (Long participantId : participantIds) {
                validateParticipant(participantId, club);
                validParticipantIds.add(participantId);
            }
        }
        return validParticipantIds;
    }

    // 참가자가 클럽의 회원인지 확인
    private void validateParticipant(Long participantId, Club club) {
        User participantUser = userRepository.findByIdAndDeletedAtIsNullOrThrow(participantId);

        // 클럽의 회원인지 확인
        boolean isMemberOfClub = club.getClubMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(participantUser.getId()));
        if (!isMemberOfClub) {
            throw new CustomException(ErrorCode.SCHEDULE_NOT_A_CLUB_MEMBER);
        }
    }

    // 생성한 모임일정에 참가자를 추가
    private void addParticipants(Schedule schedule, Set<Long> participantIds, Club club) {
        for (Long participantId : participantIds) {
            User participantUser = userRepository.findByIdAndDeletedAtIsNullOrThrow(participantId);

            // 이미 참가자인지 확인
            boolean alreadyParticipating = scheduleParticipantRepository.existsByScheduleAndUser(schedule, participantUser);
            if (alreadyParticipating) {
                throw new CustomException(ErrorCode.SCHEDULE_ALREADY_EXISTS);
            }

            ScheduleParticipant additionalParticipant = new ScheduleParticipant(schedule, participantUser, club);
            scheduleParticipantRepository.save(additionalParticipant);
        }
    }
}