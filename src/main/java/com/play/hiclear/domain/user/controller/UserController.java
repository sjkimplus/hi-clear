package com.play.hiclear.domain.user.controller;

import com.play.hiclear.common.message.SuccessMessage;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 유저 전체 조회
    @GetMapping("/v1/users")
    public ResponseEntity<Page<UserSimpleResponse>> search(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(userService.search(page, size));
    }

    // 유저 상세 조회
    @GetMapping("/v1/users/{userId}")
    public ResponseEntity<UserDetailResponse> get(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long userId
    ){
        return ResponseEntity.ok(userService.get(authUser, userId));
    }


    // 유저 정보 수정
    @PatchMapping("/v1/users")
    public ResponseEntity<UserUpdateResponse> update(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody UserUpdateRequest request
    ){
        return ResponseEntity.ok(userService.update(authUser, request));
    }

    // 유저 프로필 사진 등록
    @PostMapping("/v1/users/images")
    public ResponseEntity<String> updateImage(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam("image") MultipartFile image) {
        userService.updateImage(authUser, image);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.POSTED, "사진"));
    }

    // 유저 프로필 사진 등록
    @DeleteMapping("/v1/users/images")
    public ResponseEntity<String> deleteImage(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam String fileName) {
        userService.deleteImage(authUser, fileName);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.DELETED, "사진"));
    }
}
