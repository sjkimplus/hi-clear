package com.play.hiclear.domain.auth.controller;

import com.play.hiclear.domain.auth.dto.request.LoginRequest;
import com.play.hiclear.domain.auth.dto.request.SignupRequest;
import com.play.hiclear.domain.auth.dto.request.WithdrawalRequest;
import com.play.hiclear.domain.auth.dto.response.LoginResponse;
import com.play.hiclear.domain.auth.dto.response.SignupResponse;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/v1/auth/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    // 로그인
    @PostMapping("/v1/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // 회원탈퇴
    @GetMapping("/v1/auth/withdrawal")
    public ResponseEntity<String> withdrawal(@AuthenticationPrincipal AuthUser authUser, @RequestBody WithdrawalRequest request){
        authService.withdrawal(authUser, request);
        return ResponseEntity.ok("탈퇴 처리가 완료되었습니다.");
    }
}
