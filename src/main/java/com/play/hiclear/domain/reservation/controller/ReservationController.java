package com.play.hiclear.domain.reservation.controller;

import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.reservation.dto.request.ReservationChangeStatusRequest;
import com.play.hiclear.domain.reservation.dto.request.ReservationRequest;
import com.play.hiclear.domain.reservation.dto.request.ReservationUpdateRequest;
import com.play.hiclear.domain.reservation.dto.response.ReservationSearchDetailResponse;
import com.play.hiclear.domain.reservation.dto.response.ReservationSearchResponse;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 생성
    @PostMapping("/v1/reservations")
    public List<ReservationSearchDetailResponse> create(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody ReservationRequest request) {

        return reservationService.create(authUser, request);
    }

    // 예약 조회(단건)
    @GetMapping("/v1/reservations/{reservationId}")
    public ReservationSearchDetailResponse get(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal AuthUser authUser) {

        return reservationService.get(reservationId, authUser);
    }

    // 예약 목록 조회(다건)
    @GetMapping("/v1/reservations")
    public ResponseEntity<List<ReservationSearchResponse>> search(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long courtId,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) LocalDate date) {

        Page<ReservationSearchResponse> result = reservationService.search(authUser, page, size, courtId, status, date);

        return ResponseEntity.ok(result.getContent());
    }

    // 예약 수정
    @PutMapping("/v1/reservations/{reservationId}")
    public ReservationSearchDetailResponse update(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ReservationUpdateRequest request) {

        return reservationService.update(reservationId, authUser, request);
    }

    // 예약 삭제
    @DeleteMapping("/v1/reservations/{reservationId}")
    public ResponseEntity<String> delete(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal AuthUser authUser) {

        reservationService.delete(reservationId, authUser);

        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.DELETED, Reservation.class.getSimpleName()));
    }

    // 사장님 예약 수락/거절
    @PatchMapping("/v1/reservations/{reservationId}/status")
    public ResponseEntity<String> change(
            @PathVariable Long reservationId,
            @Valid @RequestBody ReservationChangeStatusRequest request,
            @AuthenticationPrincipal AuthUser authUser) {

        // 예약 상태 변경 서비스 호출
        reservationService.change(reservationId, authUser, request);

        if ("ACCEPTED".equals(request.getStatus())) {
            return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.RESERVATION_ACCEPTED));
        } else {
            return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.RESERVATION_REJECTED));
        }
    }
}