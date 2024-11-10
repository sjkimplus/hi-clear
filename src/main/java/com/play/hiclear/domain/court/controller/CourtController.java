package com.play.hiclear.domain.court.controller;

import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.court.dto.request.CourtCreateRequest;
import com.play.hiclear.domain.court.dto.request.CourtUpdateRequest;
import com.play.hiclear.domain.court.dto.response.CourtCreateResponse;
import com.play.hiclear.domain.court.dto.response.CourtSearchResponse;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.service.CourtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    // 코트 생성
    @PostMapping("/v1/business/gyms/{gymId}/courts")
    public ResponseEntity<CourtCreateResponse> create(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long gymId,
            @Valid @RequestBody CourtCreateRequest courtCreateRequest) {
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
            @Valid @RequestBody CourtUpdateRequest courtUpdateRequest
    ) {
        return ResponseEntity.ok(courtService.update(authUser, gymId, courtNum, courtUpdateRequest));
    }


    // 코트 삭제
    @DeleteMapping("/v1/business/gyms/{gymId}/courts")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long gymId,
            @RequestParam Long courtNum
    ) {
        courtService.delete(authUser, gymId, courtNum);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.DELETED, Court.class.getSimpleName()));
    }

}
