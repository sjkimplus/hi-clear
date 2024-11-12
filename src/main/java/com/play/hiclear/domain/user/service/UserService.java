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
import org.locationtech.jts.geom.Point;
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


    /**
     * 유저 전체 조회
     *
     * @param authUser 인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param page     조회할 페이지 번호
     * @param size     페이지당 표시할 객체 개수
     * @return 유저 목록을 반환
     */
    public Page<UserSimpleResponse> search(AuthUser authUser, int page, int size) {

        // 로그인 유저 확인
        userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<User> users = userRepository.findAllByDeletedAtIsNull(pageable);

        // User 객체를 UserSimpleResponse로 변환
        List<UserSimpleResponse> userResponses = users.getContent().stream()
                .map(user -> new UserSimpleResponse(user.getId(), user.getName(), user.getSelfRank().name(), user.getRegionAddress())) // 필요한 필드로 변환
                .collect(Collectors.toList());

        return new PageImpl<>(userResponses, pageable, users.getTotalElements());
    }

    /**
     * 유저 정보 수정
     *
     * @param authUser 인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param request  수정할 유저의 정보를 포함한 객체
     * @return 수정된 유저의 정보를 반환
     */
    @Transactional
    public UserUpdateResponse update(AuthUser authUser, UserUpdateRequest request) {

        // 유저 불러오기
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        // 지역 정보 불러오기
        GeoCodeDocument geoCodeDocument = geoCodeService.getGeoCode(request.getAddress());


        // 좌표 객체 생성
        Point location = geoCodeService.createPoint(geoCodeDocument);
        // 정보 업데이트
        user.update(
                geoCodeDocument.getRegionAddress(),
                geoCodeDocument.getRoadAddress(),
                location,
                request.getSelfRank()
        );

        // DTO 객체 반환
        return new UserUpdateResponse(
                user.getRegionAddress(),
                user.getSelfRank());
    }

    /**
     * 유저 단건 조회
     *
     * @param authUser 인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param userId   조회할 유저의 ID
     * @return 조회한 유저에대한 상세정보를 반환
     */
    public UserDetailResponse get(AuthUser authUser, Long userId) {

        // 유저 권한 확인
        userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        // 유저 불러오기
        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(userId);

        // name(email)형태의 문자열 생성
        String nameEmail = String.format("%s(%s)", user.getName(), user.getEmail());

//        reviewService.updateUserStatistics();

        // DTO 객체 생성 및 반환
        return new UserDetailResponse(
                nameEmail.toString(),
                user.getRegionAddress(),
                user.getSelfRank()
        );
    }

    /**
     * 유저 사진 등록
     *
     * @param authUser 인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param image    업로드할 이미지파일
     */
    @Transactional
    public void updateImage(AuthUser authUser, MultipartFile image) {

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        user.updateImage(s3Service.uploadFile(image));
    }


    /**
     * 유저 사진 삭제
     *
     * @param authUser 인증된 사용자 객체로, 요청을 수행하는 사용자에 대한 정보를 포함
     * @param fileName 삭제할 파일 이름 입력
     */
    @Transactional
    public void deleteImage(AuthUser authUser, String fileName) {

        User user = userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId());

        s3Service.deleteFile(fileName);

        user.updateImage(null);
    }


}
