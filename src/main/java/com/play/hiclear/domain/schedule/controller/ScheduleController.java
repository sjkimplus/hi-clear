package com.play.hiclear.domain.schedule.controller;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.utils.JwtUtil;
import com.play.hiclear.domain.schedule.dto.request.ScheduleRequest;
import com.play.hiclear.domain.schedule.dto.response.ScheduleSearchDetailResponse;
import com.play.hiclear.domain.schedule.service.ScheduleService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final JwtUtil jwtUtil;

    // 모임 일정 생성
    @PostMapping("/v1/clubs/{clubId}/schedules")
    public ResponseEntity<ScheduleSearchDetailResponse> createSchedule(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long clubId,
            @RequestBody ScheduleRequest scheduleRequestDto) {

        String email = extractEmailFromToken(authorizationHeader);

        ScheduleSearchDetailResponse createdSchedule = scheduleService.create(email, clubId, scheduleRequestDto);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
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
