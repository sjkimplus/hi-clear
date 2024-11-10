package com.play.hiclear.domain.auth.controller;

import com.play.hiclear.common.message.SuccessMessage;
import com.play.hiclear.domain.auth.dto.request.AuthDeleteRequest;
import com.play.hiclear.domain.auth.dto.request.AuthLoginRequest;
import com.play.hiclear.domain.auth.dto.request.AuthSignupRequest;
import com.play.hiclear.domain.auth.dto.response.AuthLoginResponse;
import com.play.hiclear.domain.auth.dto.response.AuthSignupResponse;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.auth.service.AuthService;
import com.play.hiclear.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/v1/auth/signup")
    public ResponseEntity<AuthSignupResponse> signup(@Valid @RequestBody AuthSignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }


    // 로그인
    @PostMapping("/v1/auth/login")
    public ResponseEntity<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    // 회원탈퇴
    @DeleteMapping("/v1/auth/withdrawals")
    public ResponseEntity<String> delete(@AuthenticationPrincipal AuthUser authUser, @RequestBody AuthDeleteRequest request) {
        authService.delete(authUser, request);
        return ResponseEntity.ok(SuccessMessage.customMessage(SuccessMessage.DELETED, User.class.getSimpleName()));
    }
}
