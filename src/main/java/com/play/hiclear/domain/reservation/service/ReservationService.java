package com.play.hiclear.domain.reservation.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.repository.CourtRepository;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.reservation.dto.request.ReservationChangeStatusRequest;
import com.play.hiclear.domain.reservation.dto.request.ReservationRequest;
import com.play.hiclear.domain.reservation.dto.request.ReservationUpdateRequest;
import com.play.hiclear.domain.reservation.dto.response.ReservationSearchDetailResponse;
import com.play.hiclear.domain.reservation.dto.response.ReservationSearchResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 예약 생성
     * @param authUser
     * @param request
     * @return List<ReservationSearchDetailResponse>
     */
    @Transactional
    public List<ReservationSearchDetailResponse> create(AuthUser authUser, ReservationRequest request) {
        log.info("예약 생성 요청 - 사용자: {}", authUser.getEmail());

        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());

        Court court = courtRepository.findByIdAndDeletedAtIsNullOrThrow(request.getCourtId());

        // 체육관의 주인인지 확인
        if (court.getGym().getUser().equals(user)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Reservation.class.getSimpleName());
        }

        // Court의 활성화/비활성화 , Court 삭제, 해당 Court가 있는 체육관이 삭제되었는지 확인
        checkCourtStatus(court);
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

        User user = userRepository.findByEmailAndDeletedAtIsNullOrThrow(authUser.getEmail());
        Reservation reservation = reservationRepository.findByIdAndUserOrThrow(reservationId, user);

        // 현재 예약이 이미 수락/삭제/거절됬는지 확인
        checkStatus(reservation);

        // 현재 예약의 시간 슬롯과 날짜
        TimeSlot originalTimeSlot = reservation.getTimeSlot();
        LocalDate originalDate = reservation.getDate();

        // 요청받은 날짜
        LocalDate updatedDate = request.getDate();

        // 요청받은 타임 슬롯과 코트 통합 조회
        TimeSlot newTimeSlot = request.getTimeId() != null ? timeSlotRepository.findByIdOrThrow(request.getTimeId()) : originalTimeSlot;
        Court newCourt = newTimeSlot.getCourt();

        // Court의 활성화/비활성화 , Court 삭제, 해당 Court가 있는 체육관이 삭제되었는지 확인
        checkCourtStatus(newCourt);

        // 날짜 수정
        if (updatedDate != null && !updatedDate.isEqual(originalDate)) {
            reservation.updateDate(updatedDate);
            log.info("예약 날짜 업데이트 - 예약 ID: {}", reservationId);
        }

        // 시간 슬롯 수정
        if (!newTimeSlot.equals(originalTimeSlot)) {
            reservation.updateTime(newTimeSlot, newCourt);
            log.info("예약 시간 업데이트 - 예약 ID: {}", reservationId);
        }

        // 중복 예약 체크
        checkDuplicateReservation(newCourt, updatedDate != null ? updatedDate : originalDate, List.of(newTimeSlot), reservationId);

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
            case REJECTED, CANCELED -> {
                throw new CustomException(ErrorCode.RESERVATION_CANT_ACCEPTED);
            }
            default -> {
                throw new CustomException(ErrorCode.RESERVATION_CANT_CANCELED);
            }
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
        Reservation reservation = reservationRepository.findByIdAndCourt_Gym_User_OrThrow(reservationId, user);

        // 현재 예약이 이미 삭제되었는지 확인
        checkCancellationEligibility(reservation);

        ReservationStatus newStatus = ReservationStatus.of(request.getStatus());
        reservation.updateStatus(newStatus);

        log.info("예약 상태 변경 완료 - 예약 ID: {}, 새로운 상태: {}", reservationId, newStatus);
    }

    // Court의 활성화/비활성화 , Court 삭제, 해당 Court가 있는 체육관이 삭제되었는지 확인
    private void checkCourtStatus(Court court) {
        if (court.getDeletedAt() != null || court.getGym().getDeletedAt() != null) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, Reservation.class.getSimpleName());
        }
    }

    // 예약 날짜가 현재 시간 이후인지 확인
    private void validateRequestDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.INVALID_DATE);
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