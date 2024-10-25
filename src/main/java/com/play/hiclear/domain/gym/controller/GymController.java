package com.play.hiclear.domain.gym.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.gym.dto.request.GymSaveRequest;
import com.play.hiclear.domain.gym.dto.request.GymUpdateRequest;
import com.play.hiclear.domain.gym.dto.response.GymSaveResponse;
import com.play.hiclear.domain.gym.dto.response.GymSimpleResponse;
import com.play.hiclear.domain.gym.dto.response.GymUpdateResponse;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.service.GymService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GymController {

    private final GymService gymService;

    // 체육관 등록
    @PostMapping("/v1/business/gyms")
    public ResponseEntity<GymSaveResponse> create(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody GymSaveRequest request) {
        return ResponseEntity.ok(gymService.create(authUser, request));
    }


    // 체육관 조회(체육관 이름, 주소, 타입으로 검색가능)
    @GetMapping("/v1/gyms")
    public ResponseEntity<Page<GymSimpleResponse>> search(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) GymType gymType
    ) {
        return ResponseEntity.ok(gymService.search(page, size, name, address, gymType));
    }


    // 체육관 조회(사업자 기준 보유 체육관)
    @GetMapping("/v1/business/gyms")
    public ResponseEntity<Page<GymSimpleResponse>> businessSearch(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(gymService.businessSearch(authUser, page, size));
    }


    // 체육관 정보 수정
    @PatchMapping("/v1/business/gyms/{gymId}")
    public ResponseEntity<GymUpdateResponse> update(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long gymId,
            @RequestBody GymUpdateRequest gymUpdateRequest
    ){
        return ResponseEntity.ok(gymService.update(authUser,gymId, gymUpdateRequest));
    }


    // 체육관 삭제
    @GetMapping("/v1/business/gyms/{gymId}")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long gymId
    ){
        gymService.delete(authUser, gymId);
        return ResponseEntity.ok("체육관이 삭제됐습니다.");
    }


}
