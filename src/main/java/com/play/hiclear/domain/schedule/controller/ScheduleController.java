package com.play.hiclear.domain.schedule.controller;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.utils.JwtUtil;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.schedule.dto.request.ScheduleRequest;
import com.play.hiclear.domain.schedule.dto.response.ScheduleSearchDetailResponse;
import com.play.hiclear.domain.schedule.dto.response.ScheduleSearchResponse;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.schedule.service.ScheduleService;
import io.jsonwebtoken.Claims;
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
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 모임 일정 생성
    @PostMapping("/v1/clubs/{clubId}/schedules")
    public ResponseEntity<ScheduleSearchDetailResponse> createSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long clubId,
            @RequestBody ScheduleRequest scheduleRequestDto) {

        ScheduleSearchDetailResponse createdSchedule = scheduleService.create(authUser.getEmail(), clubId, scheduleRequestDto);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    // 모임 일정 단건 조회
    @GetMapping("/v1/schedules/{scheduleId}")
    public ResponseEntity<ScheduleSearchDetailResponse> getSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long scheduleId) {

        ScheduleSearchDetailResponse scheduleDetail = scheduleService.get(scheduleId, authUser.getEmail());
        return ResponseEntity.ok(scheduleDetail);
    }

    // 클럽의 모임 일정 목록 조회
    @GetMapping("/v1/clubs/{clubId}/schedules")
    public ResponseEntity<List<ScheduleSearchResponse>> searchSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long clubId) {

        List<Schedule> schedules = scheduleService.search(clubId, authUser.getEmail());

        List<ScheduleSearchResponse> responseList = schedules.stream()
                .map(ScheduleSearchResponse::from)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    // 모임 일정 수정
    @PatchMapping("/v1/schedules/{scheduleId}")
    public ScheduleSearchDetailResponse updateSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long scheduleId,
            @RequestBody ScheduleRequest scheduleRequestDto) {

        return scheduleService.update(scheduleId, scheduleRequestDto, authUser.getEmail());
    }

    // 모임 일정 삭제
    @DeleteMapping("/v1/schedules/{scheduleId}")
    public ResponseEntity<Map<String, Object>> deleteSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal AuthUser authUser) {

        scheduleService.delete(scheduleId, authUser.getEmail());

        // 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.OK.value());
        response.put("message", "모일일정이 성공적으로 취소되었습니다.");
        response.put("status", HttpStatus.OK.name());

        return ResponseEntity.ok(response);
    }
}
