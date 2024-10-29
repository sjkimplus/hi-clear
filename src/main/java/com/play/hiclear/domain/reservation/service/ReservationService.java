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
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Court.class.getSimpleName()));

        // 사장님 여부 및 해당 코트 소속 체크
        if (user.getUserRole() == UserRole.BUSINESS && court.getGym().getUser().equals(user)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, "사장님은 코트 예약");
        }

        // 코트 상태 체크
        if (!court.getCourtStatus()) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, "코트가 비활성화되어 있어 예약");
        }

        // 모든 타임슬롯을 한 번에 조회
        List<TimeSlot> timeSlots = timeSlotRepository.findAllById(request.getTimeList());

        // 타임슬롯 IDs로 예약된 상태를 한 번에 확인
        List<Long> timeSlotIds = timeSlots.stream().map(TimeSlot::getId).toList();
        List<Reservation> existingReservations = reservationRepository.findByTimeSlotIdInAndStatusIn(
                timeSlotIds, List.of(ReservationStatus.PENDING, ReservationStatus.ACCEPTED));

        // 중복 예약 상태 체크
        if (!existingReservations.isEmpty()) {
            throw new CustomException(ErrorCode.TIME_SLOT_ALREADY_RESERVED);
        }

        // 타임슬롯과 코트 시간 체크
        for (TimeSlot timeSlot : timeSlots) {
            if (!timeSlot.getCourt().getId().equals(court.getId())) {
                throw new CustomException(ErrorCode.NOT_FOUND, TimeSlot.class.getSimpleName());
            }
        }

        // 예약 객체 생성
        List<Reservation> reservations = timeSlots.stream()
                .map(timeSlot -> new Reservation(user, court, timeSlot, ReservationStatus.PENDING))
                .toList();

        reservationRepository.saveAll(reservations);
        return reservations.stream().map(ReservationSearchDetailResponse::from).toList();
    }


    // 예약 조회(단건)
    public ReservationSearchDetailResponse get(Long reservationId, String email) {
        User user = findUserByEmail(email);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));

        // 예약 생성자 또는 사장님 확인
        if (!reservation.getUser().equals(user) && !reservation.getCourt().getGym().getUser().equals(user)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, "예약 조회");
        }

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

        Reservation reservation = reservationRepository.findById(reservationId)
                .filter(res -> res.getUser().equals(user))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));

        // 현재 예약 상태 체크
        if (reservation.getStatus() == ReservationStatus.ACCEPTED ||
                reservation.getStatus() == ReservationStatus.REJECTED ||
                reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, "수락, 거절, 취소된 예약 수정");
        }

        // 새로운 시간 슬롯 확인
        TimeSlot newTimeSlot = timeSlotRepository.findById(request.getTimeId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, TimeSlot.class.getSimpleName()));

        // 새로운 코트의 상태 체크
        Court newCourt = courtRepository.findById(newTimeSlot.getCourt().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Court.class.getSimpleName()));

        if (!newCourt.getCourtStatus()) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, "코트");
        }

        // 새로운 시간 슬롯의 상태를 한 번에 확인
        List<ReservationStatus> statuses = List.of(ReservationStatus.PENDING, ReservationStatus.ACCEPTED);
        boolean isTimeSlotReserved = reservationRepository.findByTimeSlotIdInAndStatusIn(
                List.of(newTimeSlot.getId()), statuses).size() > 0;

        if (isTimeSlotReserved) {
            throw new CustomException(ErrorCode.TIME_SLOT_ALREADY_RESERVED);
        }

        // 예약 수정
        reservation.update(newTimeSlot, newCourt);
        Reservation updatedReservation = reservationRepository.save(reservation);

        return ReservationSearchDetailResponse.from(updatedReservation);
    }

    // 예약 취소
    @Transactional
    public void delete(Long reservationId, String email) {
        User user = findUserByEmail(email);

        Reservation reservation = reservationRepository.findById(reservationId)
                .filter(res -> res.getUser().equals(user))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));

        // 예약 상태에 따라 취소 가능 여부 체크
        if (reservation.getStatus() == ReservationStatus.CANCELED || reservation.getStatus() == ReservationStatus.REJECTED) {
            throw new CustomException(ErrorCode.RESERVATION_CANT_CANCELED);
        }

        // PENDING 상태에서는 24시간 이내 관계없이 취소 가능
        if (reservation.getStatus() == ReservationStatus.PENDING) {
            // 예약 상태를 CANCELED로 변경
            reservation.updateStatus(ReservationStatus.CANCELED);

            // 예약 삭제 메서드 호출
            reservation.markDeleted();

            // 예약 업데이트
            reservationRepository.save(reservation);
            return;
        }

        // ACCEPTED 상태의 예약일 경우, 예약 생성 시간 체크
        LocalDateTime createdAt = reservation.getCreatedAt(); // 예약 생성 시간
        LocalDateTime reservationStartDateTime = createdAt.plusDays(2); // 2일 후의 시간

        // 현재 시간이 예약 시작 시간으로부터 24시간 이내인지 체크
        if (LocalDateTime.now().isAfter(reservationStartDateTime.minusHours(24))) {
            throw new CustomException(ErrorCode.RESERVATION_CANT_CANCELED);
        }


        // 예약 상태를 CANCELED로 변경
        reservation.updateStatus(ReservationStatus.CANCELED);

        // 예약 삭제 메서드 호출
        reservation.markDeleted();

        // 예약 업데이트
        reservationRepository.save(reservation);
    }

    // 사장님 예약 수락/거절
    @Transactional
    public void change(Long reservationId, String email, ReservationChangeStatusRequest request) {
        User user = findUserByEmail(email);

        // 사장님이 해당 체육관 소속인지 확인
        Reservation reservation = reservationRepository.findById(reservationId)
                .filter(res -> res.getCourt().getGym().getUser().equals(user))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Reservation.class.getSimpleName()));

        // 예약이 이미 취소된 경우 수락 또는 거절할 수 없음
        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new CustomException(ErrorCode.NO_AUTHORITY, "CANCELED 상태의 예약");
        }

        String status = request.getStatus();

        // 상태에 따른 로직 처리
        if (!status.equalsIgnoreCase("ACCEPTED") && !status.equalsIgnoreCase("REJECTED")) {
            throw new CustomException(ErrorCode.RESERVATION_BAD_REQUEST_ROLE);
        }

        // 상태 업데이트
        ReservationStatus newStatus = status.equalsIgnoreCase("ACCEPTED")
                ? ReservationStatus.ACCEPTED
                : ReservationStatus.REJECTED;

        // 예약 상태 업데이트
        reservation.updateStatus(newStatus);

        // 변경된 예약 정보 저장
        reservationRepository.save(reservation);

    }

    // User 조회
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, User.class.getSimpleName()));
    }
}