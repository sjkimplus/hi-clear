package com.play.hiclear.domain.auth.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.utils.JwtUtil;
import com.play.hiclear.domain.auth.dto.request.LoginRequest;
import com.play.hiclear.domain.auth.dto.request.SignupRequest;
import com.play.hiclear.domain.auth.dto.request.WithdrawalRequest;
import com.play.hiclear.domain.auth.dto.response.LoginResponse;
import com.play.hiclear.domain.auth.dto.response.SignupResponse;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AuthServiceTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private User user;
    private AuthUser authUser;

    @BeforeEach
    void setup() {
        signupRequest = new SignupRequest("test1@gamil.com", "Password!!", "홍길동", "서울특별시", "RANK_A", "BUSINESS");
        ;
        user = new User(signupRequest.getName(), signupRequest.getEmail(), signupRequest.getRegion(), "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        ReflectionTestUtils.setField(user, "id", 1L);
        authUser = new AuthUser(1L, "홍길동", "test1@gmail.com", UserRole.BUSINESS);
    }


    @Test
    void signup_success() {
        // given
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        SignupResponse result = authService.signup(signupRequest);

        // then
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(signupRequest.getEmail(), result.getEmail());
        assertEquals(signupRequest.getName(), result.getName());
        assertEquals("encodedPassword", user.getPassword());
    }


    @Test
    void signup_fail_duplicate_email() {
        // given
        SignupRequest invalidSignupRequest = new SignupRequest("test1@gamil.com", "Password!!", "김스파", "서울특별시", "RANK_B", "BUSINESS");
        ;
        when(userRepository.findByEmail(invalidSignupRequest.getEmail())).thenReturn(Optional.of(user));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.signup(invalidSignupRequest);
        });

        assertEquals("해당 이메일으로 가입된 유저가 이미 존재합니다.", exception.getMessage());

    }


    @Test
    void login_success() {
        // given
        LoginRequest loginRequest = new LoginRequest("test1@gmail.com", "Password!!");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).thenReturn("mockToken");

        // when
        LoginResponse result = authService.login(loginRequest);

        // then
        assertEquals("mockToken", result.getBearerToken());
    }


    @Test
    void login_fail_mismatch_password() {
        // given
        LoginRequest failLoginRequest = new LoginRequest("test1@gmail.com", "passworD!!");
        when(userRepository.findByEmail(failLoginRequest.getEmail())).thenReturn(Optional.of(user));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.login(failLoginRequest);
        });

        assertEquals("입력하신 비밀번호가 올바르지 않습니다. 비밀번호를 다시 확인하고 입력해 주세요.", exception.getMessage());

    }


    @Test
    void withdrawal_success() {
        // given
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest("Password!!");
        when(passwordEncoder.matches("Password!!", user.getPassword())).thenReturn(true);
        when(userRepository.findById(authUser.getUserId())).thenReturn(Optional.of(user));

        // when
        authService.withdrawal(authUser, withdrawalRequest);

        // then
        assertNotNull(user.getDeletedAt());
    }

    @Test
    void withdrawal_fail_mismatch_password() {
        // given
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest("PassworD@@");
        when(userRepository.findById(authUser.getUserId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password!!", user.getPassword())).thenReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.withdrawal(authUser, withdrawalRequest);
        });

        assertEquals("입력하신 비밀번호가 올바르지 않습니다. 비밀번호를 다시 확인하고 입력해 주세요.", exception.getMessage());

    }
}