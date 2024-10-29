package com.play.hiclear.domain.club.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.club.dto.*;
import com.play.hiclear.domain.club.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @PostMapping("/v1/clubs")
    public ResponseEntity<String> create(@AuthenticationPrincipal AuthUser authUser, @RequestBody ClubCreateRequest clubCreateRequest) {
        clubService.create(authUser.getUserId(), clubCreateRequest);
        return ResponseEntity.ok("모임이 생성되었습니다");
    }

    @GetMapping("/v1/clubs/{clubsId}")
    public ResponseEntity<ClubGetResponse> get(@PathVariable Long clubsId) {
        return ResponseEntity.ok(clubService.get(clubsId));
    }

    @PatchMapping("/v1/clubs/{clubsId}")
    public ResponseEntity<ClubUpdateResponse> update(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long clubsId,
            @RequestBody ClubUpdateRequest clubUpdateRequest
    ) throws Exception {
        return ResponseEntity.ok(clubService.update(authUser.getUserId(), clubsId, clubUpdateRequest));
    }

    @GetMapping("/v1/clubs")
    public ResponseEntity<List<ClubSearchResponse>> search() {
        return ResponseEntity.ok(clubService.search());
    }

    @DeleteMapping("/v1/clubs/{clubsId}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubsId, @RequestBody ClubDeleteRequest clubDeleteRequest) {
        clubService.delete(authUser.getUserId(), clubsId, clubDeleteRequest);
        return ResponseEntity.ok("모임을 삭제했습니다");
    }
}
