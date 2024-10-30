package com.play.hiclear.domain.reservation.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.repository.CourtRepository;
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
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CourtRepository courtRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;

    // 예약 생성
    @Transactional
    public List<ReservationSearchDetailResponse> create(String email, ReservationRequest request) {
        User user = findUserByEmail(email);
        Court court = findCourtById(request.getCourtId());
        checkCourtStatus(court);
        validateRequestDate(request.getDate());

        List<TimeSlot> timeSlots = timeSlotRepository.findAllById(request.getTimeList());
        checkTimeSlotAvailability(timeSlots);

        List<Reservation> reservations = timeSlots.stream()
                .map(timeSlot -> new Reservation(user, court, timeSlot, ReservationStatus.PENDING, request.getDate()))
                .toList();

        reservationRepository.saveAll(reservations);
        return reservations.stream().map(ReservationSearchDetailResponse::from).toList();
    }

    // 예약 조회(단건)
    public ReservationSearchDetailResponse get(Long reservationId, String email) {
        User user = findUserByEmail(email);
        Reservation reservation = findReservation(reservationId, user);
        checkReservationAuthority(reservation, user);
        return ReservationSearchDetailResponse.from(reservation);
    }

    // 예약 목록 조회(다건)
    @Transactional
    public List<ReservationSearchResponse> search(String email) {
        User user = findUserByEmail(email);
        List<Reservation> reservations = reservationRepository.findByUserWithDetails(user);

        if (reservations.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_LIST_EMPTY);
        }

        return reservations.stream()
                .map(ReservationSearchResponse::from)
                .toList();
    }

    // 예약 수정
    @Transactional
    public ReservationSearchDetailResponse update(Long reservationId, String email, ReservationUpdateRequest request) {
        User user = findUserByEmail(email);
        Reservation reservation = findReservation(reservationId, user);

        checkReservationStatusForUpdate(reservation);

        TimeSlot newTimeSlot = findTimeSlotById(request.getTimeId());
        Court newCourt = findCourtById(newTimeSlot.getCourt().getId());
        checkCourtStatus(newCourt);

        LocalDate updatedDate = request.getDate() != null ? request.getDate() : reservation.getDate();
        TimeSlot updatedTimeSlot = request.getTimeId() != null ? newTimeSlot : reservation.getTimeSlot();

        checkDuplicateReservation(newCourt, updatedDate, List.of(updatedTimeSlot), reservationId);

        if (request.getTimeId() != null) {
            reservation.updateTime(updatedTimeSlot, newCourt);
        }
        if (request.getDate() != null) {
            reservation.updateDate(updatedDate);
        }

        return ReservationSearchDetailResponse.from(reservationRepository.save(reservation));
    }

    // 예약 취소
    @Transactional
    public void delete(Long reservationId, String email) {
        User user = findUserByEmail(email);
        Reservation reservation = findReservation(reservationId, user);
        checkCancellationEligibility(reservation);

        if (reservation.getStatus() == ReservationStatus.PENDING) {
            cancelReservation(reservation);
            return;
        }

        LocalDateTime reservationDateTime = calculateReservationDateTime(reservation);
        checkCancellationTimeLimit(reservationDateTime);
        cancelReservation(reservation);
    }

    // 사장님 예약 수락/거절
    @Transactional
    public void change(Long reservationId, String email, ReservationChangeStatusRequest request) {
        User user = findUserByEmail(email);
        Reservation reservation = findReservationForBusiness(reservationId, user);
        checkCancellationEligibility(reservation);

        ReservationStatus newStatus = parseReservationStatus(request.getStatus());
        reservation.updateStatus(newStatus);
        reservationRepository.save(reservation);
    }

    // User 조회
    protected User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, User.class.getSimpleName()));
    }

    // Court 조회
    protected Court findCourtById(Long courtId) {
        return courtRepository.findById(courtId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Court.class.getSimpleName()));
    }


    // TimeSlot 조회
    protected TimeSlot findTimeSlotById(Long timeSlotId) {
        return timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, TimeSlot.class.getSimpleName()));
    }

    // Court의 활성화/비활성화 확인
    protected void checkCourtStatus(Court court) {
        if (!court.getCourtStatus()) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, "코트가 비활성화되어 있어 예약");
        }
    }

    // 예약 날짜가 현재 시간 이후인지 확인
    protected void validateRequestDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.INVALID_DATE, "예약 날짜는 현재 시간 이후여야 합니다.");
        }
    }

    // 해당 코트 시간이 이미 얘약 되었는지 확인
    protected void checkTimeSlotAvailability(List<TimeSlot> timeSlots) {
        List<Long> timeSlotIds = timeSlots.stream().map(TimeSlot::getId).toList();
        List<Reservation> existingReservations = reservationRepository.findByTimeSlotIdInAndStatusIn(
                timeSlotIds, List.of(ReservationStatus.PENDING, ReservationStatus.ACCEPTED));

        if (!existingReservations.isEmpty()) {
            throw new CustomException(ErrorCode.TIME_SLOT_ALREADY_RESERVED);
        }
    }

    // 해당 코트 시간이 해당 날짜에 이미 예약 되었는지 확인
    protected void checkDuplicateReservation(Court court, LocalDate date, List<TimeSlot> timeSlots, Long reservationId) {
        List<Reservation> existingReservations = reservationRepository.findByCourtAndDateAndTimeSlotIn(court, date, timeSlots);
        existingReservations.removeIf(existingReservation -> existingReservation.getId().equals(reservationId));

        if (!existingReservations.isEmpty()) {
            throw new CustomException(ErrorCode.TIME_SLOT_ALREADY_RESERVED);
        }
    }

    // 현재 얘약이 이미 수락/취소/거절됬는지 확인
    protected void checkReservationStatusForUpdate(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.ACCEPTED ||
                reservation.getStatus() == ReservationStatus.CANCELED ||
                reservation.getStatus() == ReservationStatus.REJECTED) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, "수락, 취소, 거절된 예약은 수정");
        }
    }

    // 현재 예약이 이미 취소/거절됬는지 확인
    protected void checkCancellationEligibility(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.CANCELED || reservation.getStatus() == ReservationStatus.REJECTED) {
            throw new CustomException(ErrorCode.RESERVATION_CANT_CANCELED);
        }
    }

    // 예약 취소하기
    private void cancelReservation(Reservation reservation) {
        reservation.updateStatus(ReservationStatus.CANCELED);
        reservation.markDeleted();
        reservationRepository.save(reservation);
    }

    // 코트를 사용할 날짜+코트를 사용할 시간(date+startTime)
    protected LocalDateTime calculateReservationDateTime(Reservation reservation) {
        return reservation.getDate()
                .atStartOfDay()
                .plusHours(reservation.getTimeSlot().getStartTime().getHour())
                .plusMinutes(reservation.getTimeSlot().getStartTime().getMinute());
    }

    // 현재 시간이 예약 시작 24시간 이내인지 확인
    protected void checkCancellationTimeLimit(LocalDateTime reservationDateTime) {
        if (LocalDateTime.now().isAfter(reservationDateTime.minusHours(24))) {
            throw new CustomException(ErrorCode.RESERVATION_CANT_CANCELED);
        }
    }

    // 예약이 존재하지 않거나, 해당 예약이 사용자의 것인지 확인
    protected Reservation findReservation(Long reservationId, User user) {
        return reservationRepository.findById(reservationId)
                .filter(res -> res.getUser().equals(user))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));
    }

    // 해당 사용자 체육관 주인(사장님)이 자신이 소속된 체육관의 예약인지
    protected Reservation findReservationForBusiness(Long reservationId, User user) {
        return reservationRepository.findById(reservationId)
                .filter(res -> res.getCourt().getGym().getUser().equals(user))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));
    }

    // 사용자가 예약을 생성한 사용자/예약이 발생한 체육관 사장님인지 조회
    protected void checkReservationAuthority(Reservation reservation, User user) {
        if (!reservation.getUser().equals(user) && !reservation.getCourt().getGym().getUser().equals(user)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, "예약 조회");
        }
    }

    // 사용자가 입력할 상태가 ACCEPTED/REJECTED인지 확인
    protected ReservationStatus parseReservationStatus(String status) {
        if (!status.equalsIgnoreCase("ACCEPTED") && !status.equalsIgnoreCase("REJECTED")) {
            throw new CustomException(ErrorCode.RESERVATION_BAD_REQUEST_ROLE);
        }
        return status.equalsIgnoreCase("ACCEPTED") ? ReservationStatus.ACCEPTED : ReservationStatus.REJECTED;
    }
}