package com.play.hiclear.domain.auth.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.common.utils.JwtUtil;
import com.play.hiclear.domain.auth.dto.request.AuthLoginRequest;
import com.play.hiclear.domain.auth.dto.request.AuthSignupRequest;
import com.play.hiclear.domain.auth.dto.request.AuthDeleteRequest;
import com.play.hiclear.domain.auth.dto.response.AuthLoginResponse;
import com.play.hiclear.domain.auth.dto.response.AuthSignupResponse;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final GeoCodeService geoCodeService;

    /**
     * 회원가입기능
     * @param request
     * @return AuthSignupResponse
     */
    @Transactional
    public AuthSignupResponse signup(AuthSignupRequest request) {

        // email을 통한 중복 가입 확인
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new CustomException(ErrorCode.AUTH_USER_EXISTING);
        }

        // 주소 입력값 확인
        GeoCodeDocument geoCodeDocument = geoCodeService.getGeoCode(request.getAddress());
        if(geoCodeDocument == null){
            throw new CustomException(ErrorCode.ADDRESS_BAD_REQUEST);
        }

        // 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(request.getPassword());

        // 유저 객체 생성
        User user = new User(
                request.getName(),
                request.getEmail(),
                geoCodeDocument.getRegionAddress(),
                geoCodeDocument.getRoadAddress(),
                geoCodeDocument.getLatitude(),
                geoCodeDocument.getLongitude(),
                encodePassword,
                Ranks.of(request.getSelfRank()),
                UserRole.of(request.getUserRole())
        );

        // 유저 DB 저장
        userRepository.save(user);

        // DTO 객체 생성 및 반환
        return new AuthSignupResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRegionAddress(),
                user.getSelfRank(),
                user.getUserRole()
        );
    }


    /**
     * 로그인(token 반환)
     * @param request
     * @return AuthLoginResponse(bearerToken)
     */
    public AuthLoginResponse login(AuthLoginRequest request) {

        // email으로 가입여부 확인
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, User.class.getSimpleName()));

        // 탈퇴여부 확인
        if (user.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.AUTH_USER_DELETED);
        }

        // 비밀번호 확인
        checkPassword(request.getPassword(), user.getPassword());

        // 토큰 생성
        String token = jwtUtil.createToken(
                user.getId(),
                user.getEmail(),
                user.getUserRole()
        );

        return new AuthLoginResponse(token);
    }


    /**
     * 회원 탈퇴(Soft Delete)
     * @param authUser
     * @param request
     */
    @Transactional
    public void delete(AuthUser authUser, AuthDeleteRequest request) {

        // 유저 조회
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        // 비밀번호 확인
        checkPassword(request.getPassword(), user.getPassword());

        // 회원 삭제
        user.markDeleted();
    }

    // 비밀번호 확인 메서드
    private void checkPassword(String requestPassword, String userPassword) {
        if (!passwordEncoder.matches(requestPassword, userPassword)) {
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST_PASSWORD);
        }
    }
}
