//package com.play.hiclear.domain.auth.service;
//
//import com.play.hiclear.common.dto.response.GeoCodeDocument;
//import com.play.hiclear.common.enums.Ranks;
//import com.play.hiclear.common.exception.CustomException;
//import com.play.hiclear.common.service.GeoCodeService;
//import com.play.hiclear.common.utils.JwtUtil;
//import com.play.hiclear.domain.auth.dto.request.AuthDeleteRequest;
//import com.play.hiclear.domain.auth.dto.request.AuthLoginRequest;
//import com.play.hiclear.domain.auth.dto.request.AuthSignupRequest;
//import com.play.hiclear.domain.auth.dto.response.AuthLoginResponse;
//import com.play.hiclear.domain.auth.dto.response.AuthSignupResponse;
//import com.play.hiclear.domain.auth.entity.AuthUser;
//import com.play.hiclear.domain.user.entity.User;
//import com.play.hiclear.domain.user.enums.UserRole;
//import com.play.hiclear.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(SpringExtension.class)
//class AuthServiceTest {
//
//    @Mock
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @Mock
//    private JwtUtil jwtUtil;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private GeoCodeService geoCodeService;
//
//    @InjectMocks
//    private AuthService authService;
//
//    private AuthSignupRequest authSignupRequest;
//    private User user;
//    private AuthUser authUser;
//
//    @BeforeEach
//    void setup() {
//        authSignupRequest = new AuthSignupRequest("test1@gamil.com", "Password!!", "홍길동", "서울 중구 태평로1가 31", "RANK_A", "BUSINESS");
//        ;
//        user = new User(authSignupRequest.getName(), authSignupRequest.getEmail(), "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
//        ReflectionTestUtils.setField(user, "id", 1L);
//        authUser = new AuthUser(1L, "홍길동", "test1@gmail.com", UserRole.BUSINESS);
//    }
//
//
//    @Test
//    void signup_success() {
//        // given
//        when(passwordEncoder.encode(authSignupRequest.getPassword())).thenReturn("encodedPassword");
//        when(userRepository.save(any(User.class))).thenReturn(user);
//        GeoCodeDocument geoCodeDocument = new GeoCodeDocument();
//        when(geoCodeService.getGeoCode(authSignupRequest.getAddress())).thenReturn(geoCodeDocument);
//
//        // when
//        AuthSignupResponse result = authService.signup(authSignupRequest);
//
//        // then
//        verify(userRepository, times(1)).save(any(User.class));
//        assertEquals(authSignupRequest.getEmail(), result.getEmail());
//        assertEquals(authSignupRequest.getName(), result.getName());
//        assertEquals("encodedPassword", user.getPassword());
//    }
//
//
//    @Test
//    void signup_fail_duplicate_email() {
//        // given
//        AuthSignupRequest invalidAuthSignupRequest = new AuthSignupRequest("test1@gamil.com", "Password!!", "김스파", "서울특별시", "RANK_B", "BUSINESS");
//        ;
//        when(userRepository.findByEmail(invalidAuthSignupRequest.getEmail())).thenReturn(Optional.of(user));
//
//        // when & then
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            authService.signup(invalidAuthSignupRequest);
//        });
//
//        assertEquals("해당 이메일으로 가입된 유저가 이미 존재합니다.", exception.getMessage());
//
//    }
//
//
//    @Test
//    void login_success() {
//        // given
//        AuthLoginRequest authLoginRequest = new AuthLoginRequest("test1@gmail.com", "Password!!");
//        when(userRepository.findByEmail(authLoginRequest.getEmail())).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(authLoginRequest.getPassword(), user.getPassword())).thenReturn(true);
//        when(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).thenReturn("mockToken");
//
//        // when
//        AuthLoginResponse result = authService.login(authLoginRequest);
//
//        // then
//        assertEquals("mockToken", result.getBearerToken());
//    }
//
//
//    @Test
//    void login_fail_mismatch_password() {
//        // given
//        AuthLoginRequest failAuthLoginRequest = new AuthLoginRequest("test1@gmail.com", "passworD!!");
//        when(userRepository.findByEmail(failAuthLoginRequest.getEmail())).thenReturn(Optional.of(user));
//
//        // when & then
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            authService.login(failAuthLoginRequest);
//        });
//
//        assertEquals("입력하신 비밀번호가 올바르지 않습니다. 비밀번호를 다시 확인하고 입력해 주세요.", exception.getMessage());
//
//    }
//
//
//    @Test
//    void delete_success() {
//        // given
//        AuthDeleteRequest authDeleteRequest = new AuthDeleteRequest("Password!!");
//        when(passwordEncoder.matches("Password!!", user.getPassword())).thenReturn(true);
//        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId())).thenReturn(user);
//
//        // when
//        authService.delete(authUser, authDeleteRequest);
//
//        // then
//        assertNotNull(user.getDeletedAt());
//    }
//
//    @Test
//    void delete_fail_mismatch_password() {
//        // given
//        AuthDeleteRequest authDeleteRequest = new AuthDeleteRequest("PassworD@@");
//        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId())).thenReturn(user);
//        when(passwordEncoder.matches("Password!!", user.getPassword())).thenReturn(true);
//
//        // when & then
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            authService.delete(authUser, authDeleteRequest);
//        });
//
//        assertEquals("입력하신 비밀번호가 올바르지 않습니다. 비밀번호를 다시 확인하고 입력해 주세요.", exception.getMessage());
//
//    }
//}