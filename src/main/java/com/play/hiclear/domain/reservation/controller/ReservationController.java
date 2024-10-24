package com.play.hiclear.domain.reservation.controller;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.utils.JwtUtil;
import com.play.hiclear.domain.reservation.dto.request.ReservationRequest;
import com.play.hiclear.domain.reservation.dto.response.ReservationResponse;
import com.play.hiclear.domain.reservation.service.ReservationService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final JwtUtil jwtUtil;

    // 예약 생성
    @PostMapping
    public List<ReservationResponse> createReservation(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ReservationRequest request) {

        String email = getEmailFromToken(authorizationHeader);

        return reservationService.createReservations(email, request);
    }
    
    // 예약 조회(단건)
    @GetMapping("/{reservationId}")
    public ReservationResponse getReservation(
            @PathVariable Long reservationId,
            @RequestHeader("Authorization") String authorizationHeader) {

        String email = getEmailFromToken(authorizationHeader);
        return reservationService.getReservation(reservationId, email);
    }

    // 예약 목록 조회(다건)
    @GetMapping
    public List<ReservationResponse> getAllReservations(
            @RequestHeader("Authorization") String authorizationHeader) {

        String email = getEmailFromToken(authorizationHeader);
        return reservationService.getAllReservations(email);
    }



    // 토큰에서 email 추출
    private String getEmailFromToken(String authorizationHeader) {
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
