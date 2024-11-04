package com.play.hiclear.domain.schedule.controller;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.common.utils.JwtUtil;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.reservation.entity.Reservation;
import com.play.hiclear.domain.schedule.dto.request.ScheduleRequest;
import com.play.hiclear.domain.schedule.dto.response.ScheduleSearchDetailResponse;
import com.play.hiclear.domain.schedule.dto.response.ScheduleSearchResponse;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.schedule.service.ScheduleService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 모임 일정 생성
    @PostMapping("/v1/clubs/{clubId}/schedules")
    public ResponseEntity<ScheduleSearchDetailResponse> create(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long clubId,
            @RequestBody ScheduleRequest scheduleRequestDto) {

        ScheduleSearchDetailResponse createdSchedule = scheduleService.create(authUser, clubId, scheduleRequestDto);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    // 모임 일정 단건 조회
    @GetMapping("/v1/schedules/{scheduleId}")
    public ResponseEntity<ScheduleSearchDetailResponse> get(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long scheduleId) {

        ScheduleSearchDetailResponse scheduleDetail = scheduleService.get(scheduleId, authUser);
        return ResponseEntity.ok(scheduleDetail);
    }

    // 클럽의 모임 일정 목록 조회
    @GetMapping("/v1/clubs/{clubId}/schedules")
    public ResponseEntity<Page<ScheduleSearchResponse>> search(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long clubId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate
    ) {

        Page<Schedule> schedules = scheduleService.search(clubId, authUser, page, size, title, description, region, startDate, endDate);

        Page<ScheduleSearchResponse> responseList = schedules.map(ScheduleSearchResponse::from);

        return ResponseEntity.ok(responseList);
    }

    // 모임 일정 수정
    @PatchMapping("/v1/schedules/{scheduleId}")
    public ScheduleSearchDetailResponse update(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long scheduleId,
            @RequestBody ScheduleRequest scheduleRequestDto) {

        return scheduleService.update(scheduleId, scheduleRequestDto, authUser);
    }

    // 모임 일정 삭제
    @DeleteMapping("/v1/schedules/{scheduleId}")
    public ResponseEntity<String> delete(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal AuthUser authUser) {

        scheduleService.delete(scheduleId, authUser);

        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.DELETED, Schedule.class.getSimpleName()));
    }
}