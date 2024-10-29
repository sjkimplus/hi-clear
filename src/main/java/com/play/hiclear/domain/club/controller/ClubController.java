package com.play.hiclear.domain.club.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.club.dto.request.ClubCreateRequest;
import com.play.hiclear.domain.club.dto.request.ClubDeleteRequest;
import com.play.hiclear.domain.club.dto.request.ClubUpdateRequest;
import com.play.hiclear.domain.club.dto.response.ClubGetResponse;
import com.play.hiclear.domain.club.dto.response.ClubSearchResponse;
import com.play.hiclear.domain.club.dto.response.ClubUpdateResponse;
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

    @GetMapping("/v1/clubs/{clubId}")
    public ResponseEntity<ClubGetResponse> get(@PathVariable Long clubId) {
        return ResponseEntity.ok(clubService.get(clubId));
    }

    @PatchMapping("/v1/clubs/{clubId}")
    public ResponseEntity<ClubUpdateResponse> update(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long clubId,
            @RequestBody ClubUpdateRequest clubUpdateRequest
    ) throws Exception {
        return ResponseEntity.ok(clubService.update(authUser.getUserId(), clubId, clubUpdateRequest));
    }

    @GetMapping("/v1/clubs")
    public ResponseEntity<List<ClubSearchResponse>> search() {
        return ResponseEntity.ok(clubService.search());
    }

    @DeleteMapping("/v1/clubs/{clubId}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long clubId, @RequestBody ClubDeleteRequest clubDeleteRequest) {
        clubService.delete(authUser.getUserId(), clubId, clubDeleteRequest);
        return ResponseEntity.ok("모임을 삭제했습니다");
    }
}
