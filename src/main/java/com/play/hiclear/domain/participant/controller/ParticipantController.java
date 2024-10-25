package com.play.hiclear.domain.participant.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.meeting.dto.request.MeetingCreateEditRequest;
import com.play.hiclear.domain.participant.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;

    @PostMapping("/v1/meetings/{meetingId}/participants")
    public ResponseEntity<String> add(@AuthenticationPrincipal AuthUser authUser,
                                      @PathVariable Long meetingId) {
        return ResponseEntity.ok(participantService.add(authUser, meetingId));
    }
}
