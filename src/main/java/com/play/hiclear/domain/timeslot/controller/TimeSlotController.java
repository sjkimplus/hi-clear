package com.play.hiclear.domain.timeslot.controller;

import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.timeslot.dto.request.TimeSlotRequest;
import com.play.hiclear.domain.timeslot.dto.response.TimeSlotResponse;
import com.play.hiclear.domain.timeslot.dto.response.TimeSlotSimpleResponse;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.timeslot.sevice.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    // 코트 시간대 생성
    @PostMapping("/v1/business/gyms/{gymId}/courts/timeslots")
    public ResponseEntity<TimeSlotResponse> create(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long gymId,
            @RequestParam Long courtNum,
            @RequestBody TimeSlotRequest timeSlotRequest
    ) {
        return ResponseEntity.ok(timeSlotService.create(authUser, gymId, courtNum, timeSlotRequest));
    }


    @GetMapping("/v1/business/gyms/{gymId}/courts/timeslots")
    public ResponseEntity<List<TimeSlotSimpleResponse>> search(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long gymId,
            @RequestParam Long courtNum
    ) {
        return ResponseEntity.ok(timeSlotService.search(authUser, gymId, courtNum));
    }


    @DeleteMapping("/v1/business/gyms/{gymId}/courts/timeslots")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long gymId,
            @RequestParam Long courtNum,
            @RequestBody TimeSlotRequest timeSlotRequest
    ) {
        timeSlotService.delete(authUser, gymId, courtNum, timeSlotRequest);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.DELETED, TimeSlot.class.getSimpleName()));
    }

}
