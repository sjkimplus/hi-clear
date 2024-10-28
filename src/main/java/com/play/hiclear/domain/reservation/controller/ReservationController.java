package com.play.hiclear.domain.reservation.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.reservation.dto.request.ReservationRequest;
import com.play.hiclear.domain.reservation.dto.request.ReservationUpdateRequest;
import com.play.hiclear.domain.reservation.dto.response.ReservationSearchDetailResponse;
import com.play.hiclear.domain.reservation.dto.response.ReservationSearchResponse;
import com.play.hiclear.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 생성
    @PostMapping("/v1/reservations")
    public List<ReservationSearchDetailResponse> createReservation(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ReservationRequest request) {

        return reservationService.create(authUser.getEmail(), request);
    }

    // 예약 조회(단건)
    @GetMapping("/v1/reservations/{reservationId}")
    public ReservationSearchDetailResponse getReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal AuthUser authUser) {

        return reservationService.get(reservationId, authUser.getEmail());
    }

    // 예약 목록 조회(다건)
    @GetMapping("/v1/reservations")
    public List<ReservationSearchResponse> searchReservations(
            @AuthenticationPrincipal AuthUser authUser) {

        return reservationService.search(authUser.getEmail());
    }

    // 예약 수정
    @PatchMapping("/v1/reservations/{reservationId}")
    public ReservationSearchDetailResponse updateReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ReservationUpdateRequest request) {

        return reservationService.update(reservationId, authUser.getEmail(), request);
    }

    // 예약 취소
    @DeleteMapping("/v1/reservations/{reservationId}")
    public ResponseEntity<Map<String, Object>> deleteReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal AuthUser authUser) {

        // 예약 취소 서비스 호출
        reservationService.delete(reservationId, authUser.getEmail());

        // 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.OK.value());
        response.put("message", "예약이 성공적으로 취소되었습니다.");  // 사용자 친화적인 메시지
        response.put("status", HttpStatus.OK.name());

        return ResponseEntity.ok(response);
    }
}
