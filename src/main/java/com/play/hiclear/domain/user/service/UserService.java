package com.play.hiclear.domain.user.service;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.user.dto.request.UserUpdateRequest;
import com.play.hiclear.domain.user.dto.response.UserDetailResponse;
import com.play.hiclear.domain.user.dto.response.UserSimpleResponse;
import com.play.hiclear.domain.user.dto.response.UserUpdateResponse;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<UserSimpleResponse> search(int page, int size) {

        Pageable pageable = PageRequest.of(page - 1 , size);

        Page<User> users = userRepository.findAll(pageable);


        // User 객체를 UserSimpleResponse로 변환
        List<UserSimpleResponse> userResponses = users.getContent().stream()
                .map(user -> new UserSimpleResponse(user.getId(), user.getName(), user.getSelfRank().name(), user.getRegion())) // 필요한 필드로 변환
                .collect(Collectors.toList());

        return new PageImpl<>(userResponses, pageable, users.getTotalElements());
    }

    @Transactional
    public UserUpdateResponse update(AuthUser authUser, UserUpdateRequest request) {

        // 유저 불러오기
        User user = userRepository.findById(authUser.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "유저를"));


        // 정보 업데이트
        user.update(
                request.getRegion(),
                request.getSelfRank()
        );

        // DTO 객체 반환
        return new UserUpdateResponse(
                user.getRegion(),
                user.getSelfRank());
    }

    public UserDetailResponse get(AuthUser authUser, Long userId) {

        // 유저 불러오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "유저를"));

        // name(email)형태의 문자열 생성
        StringBuilder nameEmail = new StringBuilder();
        nameEmail.append(user.getName()).append("(").append(user.getEmail()).append(")");

        // DTO 객체 생성 및 반환
        return new UserDetailResponse(
                nameEmail.toString(),
                user.getRegion(),
                user.getSelfRank()
        );
    }
}
