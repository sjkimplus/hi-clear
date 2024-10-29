package com.play.hiclear.domain.court.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.court.dto.request.CourtCreateRequest;
import com.play.hiclear.domain.court.dto.response.CourtCreateResponse;
import com.play.hiclear.domain.court.dto.response.CourtSearchResponse;
import com.play.hiclear.domain.court.service.CourtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    // 코트 생성
    @PostMapping("/v1/business/gyms/{gymId}/courts")
    public ResponseEntity<CourtCreateResponse> create(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long gymId,
            @RequestBody CourtCreateRequest courtCreateRequest) {
        return ResponseEntity.ok(courtService.create(authUser, gymId, courtCreateRequest));
    }


    // 코트 전체 조회
    @GetMapping("/v1/business/gyms/{gymId}/courts")
    public ResponseEntity<List<CourtSearchResponse>> search(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long gymId
    ) {
        return ResponseEntity.ok(courtService.search(authUser, gymId));
    }


    // 코트 정보 수정
    @PatchMapping("/v1/business/gyms/{gymId}/courts")
    public ResponseEntity<CourtCreateResponse> update(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long gymId,
            @RequestParam Long courtNum,
            @RequestBody CourtCreateRequest courtCreateRequest
    ) {
        return ResponseEntity.ok(courtService.update(authUser, gymId, courtNum, courtCreateRequest));
    }


    // 코트 삭제
    @DeleteMapping("/v1/business/gyms/{gymId}/courts")
    public void delete(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long gymId,
            @RequestParam Long courtNum
    ) {
        courtService.delete(authUser, gymId, courtNum);
    }
}
