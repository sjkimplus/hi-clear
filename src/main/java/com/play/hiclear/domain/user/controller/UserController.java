package com.play.hiclear.domain.user.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.user.dto.request.UserUpdateRequest;
import com.play.hiclear.domain.user.dto.response.UserDetailResponse;
import com.play.hiclear.domain.user.dto.response.UserSimpleResponse;
import com.play.hiclear.domain.user.dto.response.UserUpdateResponse;
import com.play.hiclear.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 유저 전체 조회
    @GetMapping("/v1/users")
    public ResponseEntity<Page<UserSimpleResponse>> searchUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(userService.searchUsers(page, size));
    }

    // 유조 상세 조회
    @GetMapping("/v1/users/{userId}")
    public ResponseEntity<UserDetailResponse> detailSearchUser(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long userId
    ){
        return ResponseEntity.ok(userService.detailSearchUser(authUser, userId));
    }


    // 유저 정보 수정
    @PatchMapping("/v1/users")
    public ResponseEntity<UserUpdateResponse> updateUser(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody UserUpdateRequest request
    ){
        return ResponseEntity.ok(userService.updateUser(authUser, request));
    }
}
