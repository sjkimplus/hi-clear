package com.play.hiclear.domain.user.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.service.AwsS3Service;
import com.play.hiclear.common.service.GeoCodeService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final GeoCodeService geoCodeService;
    private final AwsS3Service s3Service;

    public Page<UserSimpleResponse> search(int page, int size) {

        Pageable pageable = PageRequest.of(page - 1 , size);

        Page<User> users = userRepository.findAllByDeletedAtIsNull(pageable);

        // User 객체를 UserSimpleResponse로 변환
        List<UserSimpleResponse> userResponses = users.getContent().stream()
                .map(user -> new UserSimpleResponse(user.getId(), user.getName(), user.getSelfRank().name(), user.getRegionAddress())) // 필요한 필드로 변환
                .collect(Collectors.toList());

        return new PageImpl<>(userResponses, pageable, users.getTotalElements());
    }

    @Transactional
    public UserUpdateResponse update(AuthUser authUser, UserUpdateRequest request) {

        // 유저 불러오기
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        // 지역 정보 불러오기
        GeoCodeDocument geoCodeDocument = geoCodeService.getGeoCode(request.getAddress());

        // 정보 업데이트
        user.update(
                geoCodeDocument.getRegionAddress(),
                geoCodeDocument.getRoadAddress(),
                geoCodeDocument.getLatitude(),
                geoCodeDocument.getLongitude(),
                request.getSelfRank()
        );

        // DTO 객체 반환
        return new UserUpdateResponse(
                user.getRegionAddress(),
                user.getSelfRank());
    }

    public UserDetailResponse get(AuthUser authUser, Long userId) {

        // 유저 불러오기
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);

        // name(email)형태의 문자열 생성
        StringBuilder nameEmail = new StringBuilder();
        nameEmail.append(user.getName()).append("(").append(user.getEmail()).append(")");

//        reviewService.updateUserStatistics();

        // DTO 객체 생성 및 반환
        return new UserDetailResponse(
                nameEmail.toString(),
                user.getRegionAddress(),
                user.getSelfRank()
        );
    }

    @Transactional
    public void updateImage(AuthUser authUser, MultipartFile image) {

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        user.updateImage(s3Service.uploadFile(image));
    }


    @Transactional
    public void deleteImage(AuthUser authUser, String fileName) {

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        s3Service.deleteFile(fileName);

        user.updateImage(null);
    }
}
