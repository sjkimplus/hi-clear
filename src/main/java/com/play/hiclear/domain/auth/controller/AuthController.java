package com.play.hiclear.domain.auth.controller;

import com.play.hiclear.domain.auth.dto.request.SignupRequest;
import com.play.hiclear.domain.auth.dto.response.SignupResponse;
import com.play.hiclear.domain.auth.service.AuthService;
import com.play.hiclear.domain.user.dto.request.UserSaveRequest;
import com.play.hiclear.domain.user.dto.response.UserSaveResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/v1/auth/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request){
        return ResponseEntity.ok(authService.signup(request));
    }
}
