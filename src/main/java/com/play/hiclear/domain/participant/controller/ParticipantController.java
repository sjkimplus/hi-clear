package com.play.hiclear.domain.participant.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.participant.dto.ParticipantResponse;
import com.play.hiclear.domain.participant.dto.ParticipantUpdateRequest;
import com.play.hiclear.domain.participant.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;

    // 번개 참여 신청
    @PostMapping("/v1/meetings/{meetingId}/participants")
    public ResponseEntity<String> add(@AuthenticationPrincipal AuthUser authUser,
                                      @PathVariable Long meetingId) {
        return ResponseEntity.ok(participantService.add(authUser, meetingId));
    }

    // 번개 참여 신청 철회/거절/승락
    @PatchMapping("/v1/meetings/{meetingId}/participants")
    public ResponseEntity<String> update(@AuthenticationPrincipal AuthUser authUser,
                                         @PathVariable Long meetingId,
                                         @RequestBody ParticipantUpdateRequest request) {
        return ResponseEntity.ok(participantService.update(authUser, meetingId, request));
    }

    // 번개 참여자 리스트 조회
    @GetMapping("/v1/meetings/{meetingId}/participants")
    public ResponseEntity<List<ParticipantResponse>> search(@PathVariable Long meetingId) {
        return ResponseEntity.ok(participantService.search(meetingId));
    }
}
