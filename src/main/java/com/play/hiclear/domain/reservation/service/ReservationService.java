package com.play.hiclear.domain.reservation.service;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.repository.CourtRepository;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.reservation.repository.ReservationRepository;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.timeslot.repository.TimeSlotRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.play.hiclear.domain.reservation.dto.response.*;
import com.play.hiclear.domain.reservation.dto.request.*;
import com.play.hiclear.common.exception.*;
import org.springframework.data.domain.*;

import java.time.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CourtRepository courtRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 예약 생성
     * @param authUser
     * @param request
     * @return
     */
    @Transactional
    public List<ReservationSearchDetailResponse> create(AuthUser authUser, ReservationRequest request) {
        String lockKey = "reservation-lock:" + request.getCourtId() + ":" + request.getDate().atStartOfDay();  // 날짜와 시간을 정확히 포함
        String lockValue = "locked";  // 락 값
        long lockExpiration = 10L;  // 락 만료 시간 (10초)

        // 락 획득 시도
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, lockExpiration, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(success)) {
            // 락을 획득한 경우
            try {
                // 예약 작업 시작
                log.info("예약 작업 시작: {} {}", request.getCourtId(), request.getDate());

                log.info("예약 생성 요청 - 사용자: {}", authUser.getEmail());

                User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());

                Court court = courtRepository.findByIdAndDeletedAtIsNullOrThrow(request.getCourtId());

                // 해당 코트가 속한 체육관의 타입이 PRIVATE인지 확인
                checkGym(court);

                // 체육관의 주인인지 확인
                if (court.getGym().getUser().equals(user)) {
                    throw new CustomException(ErrorCode.NO_AUTHORITY, Reservation.class.getSimpleName());
                }

                // 예약 날짜가 현재 시간 이후인지 확인
                validateRequestDate(request.getDate());

                List<TimeSlot> timeSlots = timeSlotRepository.findAllById(request.getTimeList());

                // 해당 코트 시간이 이미 얘약 되었는지 확인
                checkTimeSlotAvailability(timeSlots, request.getDate());

                // 요청된 시간 슬롯이 해당 코트와 맞는지 확인
                validateTimeSlotsForCourt(court, timeSlots);

                List<Reservation> reservations = timeSlots.stream()
                        .map(timeSlot -> new Reservation(user, court, timeSlot, ReservationStatus.PENDING, request.getDate()))
                        .toList();

                reservationRepository.saveAll(reservations);

                log.info("예약 생성 완료 - 예약 수: {}", reservations.size());
                return reservations.stream().map(ReservationSearchDetailResponse::from).toList();

            } catch (Exception e) {
                // 예약 작업 중 오류가 발생한 경우
                log.error("예약 작업 중 오류 발생: {}", e.getMessage(), e);
                throw e;  // 예외를 다시 던짐
            } finally {
                // 작업 완료 후 락 해제
                redisTemplate.delete(lockKey);
                log.info("예약 작업 완료, 락 해제: {} {}", request.getCourtId(), request.getDate());
            }
        }

        // 락을 획득하지 못한 경우 바로 예외 던짐
        log.error("락을 획득할 수 없습니다. 예약 처리 중 다른 프로세스가 작업을 진행 중입니다.");
        throw new CustomException(ErrorCode.RESERVATION_LOCK_CONFLICT);
    }

    /**
     * 예약 조회(단건)
     * @param reservationId
     * @param authUser
     * @return ReservationSearchDetailResponse
     */
    public ReservationSearchDetailResponse get(Long reservationId, AuthUser authUser) {
        log.info("예약 조회 요청 - 예약 ID: {}, 사용자: {}", reservationId, authUser.getEmail());

        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());
        Reservation reservation = reservationRepository.findByIdAndDeletedAtIsNullOrThrow(reservationId);

        // 사용자가 예약 소유자이거나 코트의 사장님인지 확인
        checkReservationAuthority(reservation, user);

        log.info("예약 조회 완료 - 예약 ID: {}", reservationId);
        return ReservationSearchDetailResponse.from(reservation);
    }

    /**
     * // 예약 목록 조회(다건)
     * @param authUser
     * @param page
     * @param size
     * @param courtId
     * @param status
     * @param date
     * @return Page<ReservationSearchResponse>
     */
    @Transactional
    public Page<ReservationSearchResponse> search(AuthUser authUser, int page, int size, Long courtId, ReservationStatus status, LocalDate date) {
        log.info("예약 목록 조회 요청 - 사용자: {}", authUser.getEmail());

        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Reservation> reservations;

        // 사용자 역할에 따라 조회 방식 결정
        if (user.getUserRole() == UserRole.BUSINESS) {
            Gym gym = gymRepository.findByUserAndDeletedAtIsNullOrThrow(user);
            // 코트 소유자는 자신이 소속된 모든 코트의 예약을 조회
            reservations = reservationRepository.findByGymUserAndDeletedAtIsNull(gym.getUser(), courtId, status, date, pageable);
        } else {
            // 일반 사용자는 자신의 예약만 조회
            reservations = reservationRepository.findByUserAndCriteriaAndDeletedAtIsNull(user, courtId, status, date, pageable);
        }

        log.info("예약 목록 조회 완료 - 예약 수: {}", reservations.getTotalElements());
        return reservations.map(ReservationSearchResponse::from);
    }

    /**
     * 예약 수정
     * @param reservationId
     * @param authUser
     * @param request
     * @return ReservationSearchDetailResponse
     */
    @Transactional
    public ReservationSearchDetailResponse update(Long reservationId, AuthUser authUser, ReservationUpdateRequest request) {
        log.info("예약 수정 요청 - 예약 ID: {}, 사용자: {}", reservationId, authUser.getEmail());

        // 사용자 정보 가져오기
        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());

        // 예약 정보 가져오기
        Reservation reservation = reservationRepository.findByIdAndUserOrThrow(reservationId, user);

        // 현재 예약 상태가 ACCEPTED, CANCELED, REJECTED 인지 확인 (수정 불가 상태)
        checkStatus(reservation);

        // 기존 예약의 시간 슬롯과 날짜
        TimeSlot originalTimeSlot = reservation.getTimeSlot();
        LocalDate originalDate = reservation.getDate();

        // 요청 받은 날짜, 날짜가 null일 경우 기존 날짜 사용
        LocalDate updatedDate = request.getDate() != null ? request.getDate() : originalDate;

        // 날짜가 현재 시간 이후인지 확인
        validateRequestDate(updatedDate);

        // 요청 받은 타임 슬롯과 코트 조회
        TimeSlot newTimeSlot = request.getTimeId() != null ? timeSlotRepository.findByIdOrThrow(request.getTimeId()) : originalTimeSlot;
        Court newCourt = newTimeSlot.getCourt();

        // 해당 코트가 속한 체육관의 타입이 PRIVATE인지 확인
        checkGym(newCourt);

        // 날짜 수정 (request에 date가 없으면 기존 날짜를 그대로 사용)
        if (!updatedDate.isEqual(originalDate)) {
            reservation.updateDate(updatedDate);
            log.info("예약 날짜 업데이트 - 예약 ID: {}", reservationId);
        }

        // 시간 슬롯 수정 (request에 timeId가 없으면 기존 시간 슬롯 그대로 사용)
        if (!newTimeSlot.equals(originalTimeSlot)) {
            reservation.updateTime(newTimeSlot, newCourt);
            log.info("예약 시간 업데이트 - 예약 ID: {}", reservationId);
        }

        // 중복 예약 체크
        checkDuplicateReservation(newCourt, updatedDate, List.of(newTimeSlot), reservationId);

        // 예약 업데이트
        return ReservationSearchDetailResponse.from(reservationRepository.save(reservation));
    }

    /**
     * 예약 삭제
     * @param reservationId
     * @param authUser
     */
    @Transactional
    public void delete(Long reservationId, AuthUser authUser) {
        log.info("예약 삭제 요청 - 예약 ID: {}, 사용자: {}", reservationId, authUser.getEmail());

        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());
        Reservation reservation = reservationRepository.findByIdAndUserOrThrow(reservationId, user);

        LocalDateTime reservationDateTime = calculateReservationDateTime(reservation);

        switch (reservation.getStatus()) {
            case PENDING -> {
                // PENDING 상태에서는 예약 삭제 가능
                deleteReservation(reservation);
                log.info("예약 삭제 완료 - 예약 ID: {}", reservationId);
            }
            case ACCEPTED -> {
                // ACCEPTED 상태일 때 24시간 이내 여부 체크
                checkCancellationTimeLimit(reservationDateTime);
                // 예약 삭제
                deleteReservation(reservation);
                log.info("예약 삭제 완료 - 예약 ID: {}", reservationId);
            }
            case REJECTED, CANCELED -> throw new CustomException(ErrorCode.RESERVATION_CANT_ACCEPTED);
            default -> throw new CustomException(ErrorCode.RESERVATION_CANT_CANCELED);
        }
    }

    /**
     * 사장님 예약 수락/거절
     * @param reservationId
     * @param authUser
     * @param request
     */
    @Transactional
    public void change(Long reservationId, AuthUser authUser, ReservationChangeStatusRequest request) {
        log.info("예약 상태 변경 요청 - 예약 ID: {}, 사용자: {}, 상태: {}", reservationId, authUser.getEmail(), request.getStatus());

        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());
        Reservation reservation = reservationRepository.findByIdAndCourtGymUserOrThrow(reservationId, user);

        // 현재 예약이 이미 삭제되었는지 확인
        checkCancellationEligibility(reservation);

        ReservationStatus newStatus = ReservationStatus.of(request.getStatus());
        reservation.updateStatus(newStatus);

        log.info("예약 상태 변경 완료 - 예약 ID: {}, 새로운 상태: {}", reservationId, newStatus);
    }

//    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
//    @Transactional
//    public void deleteExpiredReservations() {
//        log.info("예약 만료 처리 시작");
//
//        // 현재 시간
//        LocalDateTime now = LocalDateTime.now();
//
//        // LocalDate와 LocalTime을 각각 추출
//        LocalDate nowDate = now.toLocalDate();  // 현재 날짜
//        LocalTime nowTime = now.toLocalTime();  // 현재 시간
//
//        // 예약 시간이 지난 예약 List
//        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations(nowDate, nowTime);
//
//        if (expiredReservations.isEmpty()) {
//            log.info("만료된 예약이 없습니다.");
//        } else {
//            // 만료된 예약들을 삭제
//            reservationRepository.deleteAll(expiredReservations);
//            log.info("{}개의 만료된 예약이 삭제", expiredReservations.size());
//
//            // 삭제된 예약에 대해 로그 출력
//            expiredReservations.forEach(reservation ->
//                    log.info("만료된 예약 삭제 - 예약 ID: {}", reservation.getId()));
//        }
//    }

    // 예약 날짜가 현재 시간 이후인지 확인
    private void validateRequestDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.INVALID_DATE);
        }
    }

    // 해당 코트가 속한 체육관의 타입이 PUBLIC인지 확인
    private void checkGym(Court court) {
        if (court.getGym().getGymType() != GymType.PRIVATE) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Gym.class.getSimpleName());
        }

    }

    // 특정 시간 슬롯들이 이미 예약되었는지를 확인
    private void checkTimeSlotAvailability(List<TimeSlot> timeSlots, LocalDate date) {
        List<Long> timeSlotIds = timeSlots.stream().map(TimeSlot::getId).toList();
        List<Reservation> existingReservations = reservationRepository.findByTimeSlotIdInAndStatusInAndDate(
                timeSlotIds, List.of(ReservationStatus.PENDING, ReservationStatus.ACCEPTED), date);

        if (!existingReservations.isEmpty()) {
            throw new CustomException(ErrorCode.TIME_SLOT_ALREADY_RESERVED);
        }
    }


    // 특정 코트와 날짜에 대해 시간 슬롯이 이미 예약
    private void checkDuplicateReservation(Court court, LocalDate date, List<TimeSlot> timeSlots, Long reservationId) {
        List<Reservation> existingReservations = reservationRepository.findByCourtAndDateAndTimeSlotIn(court, date, timeSlots);
        existingReservations.removeIf(existingReservation -> existingReservation.getId().equals(reservationId));

        if (!existingReservations.isEmpty()) {
            throw new CustomException(ErrorCode.TIME_SLOT_ALREADY_RESERVED);
        }
    }

    // 요청된 시간 슬롯이 해당 코트와 맞는지 확인
    private void validateTimeSlotsForCourt(Court court, List<TimeSlot> timeSlots) {
        for (TimeSlot timeSlot : timeSlots) {
            if (!timeSlot.getCourt().equals(court)) {
                throw new CustomException(ErrorCode.INVALID_TIME_SLOT);
            }
        }
    }

    // 현재 예약이 이미 수락/삭제/거절됬는지 확인
    private void checkStatus(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.ACCEPTED ||
                reservation.getStatus() == ReservationStatus.CANCELED ||
                reservation.getStatus() == ReservationStatus.REJECTED) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Reservation.class.getSimpleName());
        }
    }

    // 현재 예약이 이미 삭제되었는지 확인
    private void checkCancellationEligibility(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new CustomException(ErrorCode.RESERVATION_CANT_ACCEPTED);
        }
    }

    // 예약 삭제하기
    private void deleteReservation(Reservation reservation) {
        reservation.updateStatus(ReservationStatus.CANCELED);
        reservation.markDeleted();
    }

    // 코트를 사용할 날짜+코트를 사용할 시간(date+startTime)
    private LocalDateTime calculateReservationDateTime(Reservation reservation) {
        return reservation.getDate()
                .atStartOfDay()
                .plusHours(reservation.getTimeSlot().getStartTime().getHour())
                .plusMinutes(reservation.getTimeSlot().getStartTime().getMinute());
    }

    // 현재 시간이 예약 시작 24시간 이내인지 확인
    private void checkCancellationTimeLimit(LocalDateTime reservationDateTime) {
        if (LocalDateTime.now().isAfter(reservationDateTime.minusHours(24))) {
            throw new CustomException(ErrorCode.TIME_IS_ALREAY_PASSED);
        }
    }

    // 사용자가 예약을 생성한 사용자/예약이 발생한 체육관 사장님인지 조회
    private void checkReservationAuthority(Reservation reservation, User user) {
        if (!reservation.getUser().equals(user) && !reservation.getCourt().getGym().getUser().equals(user)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Reservation.class.getSimpleName());
        }
    }
}