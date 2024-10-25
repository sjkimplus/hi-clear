package com.play.hiclear.domain.reservation.controller;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.utils.JwtUtil;
import com.play.hiclear.domain.reservation.dto.request.ReservationRequest;
import com.play.hiclear.domain.reservation.dto.request.ReservationUpdateRequest;
import com.play.hiclear.domain.reservation.dto.response.ReservationSearchDetailResponse;
import com.play.hiclear.domain.reservation.dto.response.ReservationSearchResponse;
import com.play.hiclear.domain.reservation.service.ReservationService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final JwtUtil jwtUtil;

    // 예약 생성
    @PostMapping("/v1/reservations")
    public List<ReservationSearchDetailResponse> createReservation(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ReservationRequest request) {

        String email = extractEmailFromToken(authorizationHeader);

        return reservationService.create(email, request);
    }
    
    // 예약 조회(단건)
    @GetMapping("/v1/reservations/{reservationId}")
    public ReservationSearchDetailResponse getReservation(
            @PathVariable Long reservationId,
            @RequestHeader("Authorization") String authorizationHeader) {

        String email = extractEmailFromToken(authorizationHeader);
        return reservationService.get(reservationId, email);
    }

    // 예약 목록 조회(다건)
    @GetMapping("/v1/reservations")
    public List<ReservationSearchResponse> searchReservations(
            @RequestHeader("Authorization") String authorizationHeader) {

        String email = extractEmailFromToken(authorizationHeader);
        return reservationService.search(email);
    }

    // 예약 수정
    @PatchMapping("/v1/reservations/{reservationId}")
    public ReservationSearchDetailResponse updateReservation(
            @PathVariable Long reservationId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ReservationUpdateRequest request) {

        String email = extractEmailFromToken(authorizationHeader);
        return reservationService.update(reservationId, email, request);
    }

    // 예약 취소
    @DeleteMapping("/v1/reservations/{reservationId}")
    public ResponseEntity<Map<String, Object>> deleteReservation(
            @PathVariable Long reservationId,
            @RequestHeader("Authorization") String authorizationHeader) {

        String email = extractEmailFromToken(authorizationHeader);
        reservationService.delete(reservationId, email);

        // 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.OK.value());
        response.put("message", ErrorCode.RESERVATION_CANCELED.getMessage());
        response.put("status", HttpStatus.OK.name());

        return ResponseEntity.ok(response);
    }


    // 토큰에서 email 추출
    private String extractEmailFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.AUTH_USER_NOT_FOUND, "사용자가 인증되지 않았습니다.");
        }

        String token = jwtUtil.substringToken(authorizationHeader);
        Claims claims = jwtUtil.extractClaims(token);
        String email = claims.get("email", String.class);

        if (email == null) {
            throw new CustomException(ErrorCode.AUTH_USER_NOT_FOUND, "사용자가 인증되지 않았습니다.");
        }

        return email;
    }
}
