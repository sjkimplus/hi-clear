package com.play.hiclear.domain.schedule.controller;

import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.schedule.dto.request.ScheduleRequest;
import com.play.hiclear.domain.schedule.dto.request.ScheduleUpdateRequest;
import com.play.hiclear.domain.schedule.dto.response.ScheduleSearchDetailResponse;
import com.play.hiclear.domain.schedule.dto.response.ScheduleSearchResponse;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 모임 일정 생성
    @PostMapping("/v1/clubs/{clubId}/schedules")
    public ResponseEntity<ScheduleSearchDetailResponse> create(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long clubId,
            @Valid @RequestBody ScheduleRequest scheduleRequestDto) {

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
    public ResponseEntity<List<ScheduleSearchResponse>> search(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long clubId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String regionAddress,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate
    ) {
        // 클럽의 일정 목록을 페이징하여 가져옵니다.
        Page<Schedule> schedules = scheduleService.search(clubId, authUser, page, size, title, description, regionAddress, startDate, endDate);

        // Schedule -> ScheduleSearchResponse로 변환
        List<ScheduleSearchResponse> responseList = schedules.getContent().stream()
                .map(ScheduleSearchResponse::from)
                .toList();

        // 페이징 정보 없이 content만 반환
        return ResponseEntity.ok(responseList);
    }

    // 모임 일정 수정
    @PatchMapping("/v1/schedules/{scheduleId}")
    public ScheduleSearchDetailResponse update(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long scheduleId,
            @RequestBody ScheduleUpdateRequest scheduleUpdateRequest) {

        return scheduleService.update(scheduleId, scheduleUpdateRequest, authUser);
    }

    // 모임 일정 삭제
    @DeleteMapping("/v1/schedules/{scheduleId}")
    public ResponseEntity<String> delete(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal AuthUser authUser) {

        scheduleService.delete(scheduleId, authUser);

        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.DELETED, Schedule.class.getSimpleName()));
    }

    // 모임 일정에 참가자 추가
    @PostMapping("/v1/schedules/{scheduleId}/participants/{participantId}")
    public ResponseEntity<String> addParticipant(
            @PathVariable Long scheduleId,
            @PathVariable Long participantId,
            @AuthenticationPrincipal AuthUser authUser) {

        // 참가자 추가 로직
        scheduleService.addParticipant(scheduleId, participantId, authUser);

        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.SCHEDULE_ADDED));
    }

    // 모임 일정에 참가자 삭제
    @DeleteMapping("/v1/schedules/{scheduleId}/participants/{participantId}")
    public ResponseEntity<String> removeParticipant(
            @PathVariable Long scheduleId,
            @PathVariable Long participantId,
            @AuthenticationPrincipal AuthUser authUser) {

        // 참가자 삭제 로직
        scheduleService.deleteParticipant(scheduleId, participantId, authUser);

        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.SCHEDULE_DELETED));
    }
}