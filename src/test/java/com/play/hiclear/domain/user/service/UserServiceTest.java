package com.play.hiclear.domain.user.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.service.AwsS3Service;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.user.dto.request.UserUpdateRequest;
import com.play.hiclear.domain.user.dto.response.UserDetailResponse;
import com.play.hiclear.domain.user.dto.response.UserSimpleResponse;
import com.play.hiclear.domain.user.dto.response.UserUpdateResponse;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import com.play.hiclear.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GeoCodeService geoCodeService;

    @Mock
    private AwsS3Service awsS3Service; // S3 서비스 Mock

    @Mock
    private MultipartFile image; // 테스트용 이미지 파일 Mock

    private AuthUser authUser;

    @BeforeEach
    void setup() {
        authUser = new AuthUser(1L, "홍길동", "test1@gmail.com", UserRole.BUSINESS);
    }


    @Test
    void search_success() {
        // given
        List<User> userList = new ArrayList<>();
        User user1 = new User("홍길동", "test1@gmail.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        User user2 = new User("김스파", "test2@gmail.com", "서울특별시 강남구 삼성동 159-1", "서울특별시 강남구 봉은사로 524", 37.5128320848839, 127.057250899584, "encodedPassword", Ranks.RANK_B, UserRole.BUSINESS);
        userList.add(user1);
        userList.add(user2);

        int page = 1;
        int size = 10;
        Page<User> userPage = new PageImpl<>(userList, PageRequest.of(page - 1, size), userList.size());
        when(userRepository.findAllByDeletedAtIsNull(PageRequest.of(page - 1, size))).thenReturn(userPage);


        // when
        Page<UserSimpleResponse> result = userService.search(page, size);

        // then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("홍길동", result.getContent().get(0).getName());
        assertEquals(Ranks.RANK_A.name(), result.getContent().get(0).getSelfRank());
        assertEquals("서울 중구 태평로1가 31", result.getContent().get(0).getRegionAddress());
        assertEquals("김스파", result.getContent().get(1).getName());
        assertEquals(Ranks.RANK_B.name(), result.getContent().get(1).getSelfRank());
        assertEquals("서울특별시 강남구 삼성동 159-1", result.getContent().get(1).getRegionAddress());
    }


    @Test
    void update_success() {
        // given
        User user = new User("홍길동", "test1@gmail.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(any(Long.class))).thenReturn(user);
        GeoCodeDocument geoCodeDocument = new GeoCodeDocument();
        GeoCodeDocument.NestedAddress1 nestedAddress1 = new GeoCodeDocument.NestedAddress1();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("서울특별시 강남구 삼성동 159-1", "RANK_C");
        ReflectionTestUtils.setField(nestedAddress1, "addressName", userUpdateRequest.getAddress());
        ReflectionTestUtils.setField(geoCodeDocument, "regionAddress", nestedAddress1);
        when(geoCodeService.getGeoCode(userUpdateRequest.getAddress())).thenReturn(geoCodeDocument);

        // when
        UserUpdateResponse result = userService.update(authUser, userUpdateRequest);

        // then
        assertEquals(userUpdateRequest.getAddress(), result.getRegionAddress());
        assertEquals(Ranks.RANK_C, result.getSelfRank());
    }


    @Test
    void get_success() {
        // given
        User user = new User("홍길동", "test1@gmail.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(1L)).thenReturn(user);

        // when
        UserDetailResponse result = userService.get(authUser, 1L);

        // then
        assertNotNull(result);
        assertEquals(user.getName() + "(" + user.getEmail() + ")", result.getNameEmail());
        assertEquals(user.getRegionAddress(), result.getRegion());
        assertEquals(user.getSelfRank(), result.getSelfRank());
    }


    @Test
    void updateImage_success() {
        // given
        User user = new User("홍길동", "test1@gmail.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(any(Long.class))).thenReturn(user);
        when(awsS3Service.uploadFile(image)).thenReturn("imgurl");

        // when
        userService.updateImage(authUser, image);

        // then
        verify(awsS3Service).uploadFile(image);
        assertEquals("imgurl", user.getImgUrl());
    }


    @Test
    void deleteImage_success() {
        // given
        User user = new User("홍길동", "test1@gmail.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(any(Long.class))).thenReturn(user);
        ReflectionTestUtils.setField(user, "imgUrl", "imgurl");
        doNothing().when(awsS3Service).deleteFile("imgurl");

        // when
        userService.deleteImage(authUser, "imgurl");

        // then
        verify(awsS3Service).deleteFile("imgurl");
        assertNull(user.getImgUrl());
    }
}