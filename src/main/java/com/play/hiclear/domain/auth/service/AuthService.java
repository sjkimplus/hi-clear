package com.play.hiclear.domain.auth.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.common.utils.JwtUtil;
import com.play.hiclear.domain.auth.dto.request.LoginRequest;
import com.play.hiclear.domain.auth.dto.request.SignupRequest;
import com.play.hiclear.domain.auth.dto.response.LoginResponse;
import com.play.hiclear.domain.auth.dto.response.SignupResponse;
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

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        // 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(request.getPassword());

        // email을 통한 중복 가입 확인
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if(existingUser.isPresent()){
            throw new CustomException(ErrorCode.AUTH_USER_EXISTING);
        }

        // 유저 객체 생성
        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getRegion(),
                encodePassword,
                Ranks.of(request.getSelectRank()),
                UserRole.of(request.getUserRole())
        );

        // 유저 DB 저장
        userRepository.save(user);

        // DTO 객체 생성 및 반환
        return new SignupResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRegion(),
                user.getSelectRank(),
                user.getUserRole()
        );
    }

    public LoginResponse login(LoginRequest request) {

        // email으로 가입여부 홧인
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_USER_NOT_FOUND));

        // 비밀번호 확인
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST_PASSWORD);
        }

        // 탈퇴여부 확인
        if(user.getDeletedAt() != null){
            throw new CustomException(ErrorCode.AUTH_USER_DELETED);
        }

        // 토큰 생성
        String token = jwtUtil.createToken(
                user.getId(),
                user.getEmail(),
                user.getUserRole()
        );

        return new LoginResponse(token);
    }

}
