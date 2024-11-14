package com.play.hiclear.domain.gym.service;

import com.play.hiclear.common.dto.response.GeoCodeDocument;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.service.GeoCodeService;
import com.play.hiclear.common.utils.DistanceCalculator;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.gym.dto.request.GymCreateRequest;
import com.play.hiclear.domain.gym.dto.request.GymUpdateRequest;
import com.play.hiclear.domain.gym.dto.response.GymCreateResponse;
import com.play.hiclear.domain.gym.dto.response.GymSimpleResponse;
import com.play.hiclear.domain.gym.dto.response.GymUpdateResponse;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class GymServiceTest {

    @InjectMocks
    private GymService gymService;

    @Mock
    private GymRepository gymRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GeoCodeService geoCodeService;

    @Mock
    private GeoCodeDocument geoCodeDocument;

    @Mock
    private DistanceCalculator distanceCalculator;

    private GymCreateRequest gymCreateRequest;
    private AuthUser authUser;
    private User user;

    @BeforeEach
    void setup() {
        authUser = new AuthUser(1L, "사업자1", "test1@gmail.com", UserRole.BUSINESS);
        gymCreateRequest = new GymCreateRequest("공공체육관1", "서울 중구 태평로1가 31", "공공체육관1 설명", "PUBLIC");
        user = new User(authUser.getName(), authUser.getEmail(), "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        ReflectionTestUtils.setField(user, "id", 1L);

    }


    @Test
    void create_success() {
        // given
        geoCodeDocument = new GeoCodeDocument();
        GeoCodeDocument.NestedAddress1 address = new GeoCodeDocument.NestedAddress1();
        ReflectionTestUtils.setField(address, "addressName", "서울 중구 태평로1가 31");
        ReflectionTestUtils.setField(geoCodeDocument, "regionAddress", address);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(geoCodeService.getGeoCode(gymCreateRequest.getAddress())).thenReturn(geoCodeDocument);

        // when
        GymCreateResponse result = gymService.create(authUser, gymCreateRequest);

        // then
        verify(gymRepository, times(1)).save(any(Gym.class));
        assertEquals(gymCreateRequest.getName(), result.getName());
        assertEquals(gymCreateRequest.getAddress(), result.getRegionAddress());
        assertEquals(gymCreateRequest.getDescription(), result.getDescription());
        assertEquals(gymCreateRequest.getGymType(), result.getGymType().name());
    }


    @Test
    void search_success() {
        // given
        Gym gym1 = new Gym("공공체육관1", "공공체육관 설명1", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, GymType.PUBLIC, user);
        Gym gym2 = new Gym("공공체육관2", "공공체육관 설명2", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, GymType.PUBLIC, user);
        Gym gym3 = new Gym("사설체육관1", "사설체육관 설명1", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, GymType.PRIVATE, user);
        List<Gym> gymPage = Arrays.asList(gym1, gym2, gym3);
        when(gymRepository.search(null, null, null, user.getLatitude(), user.getLongitude(), 100d)).thenReturn(gymPage);
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId())).thenReturn(user);
        when(distanceCalculator.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(BigDecimal.valueOf(0));

        // when
        Page<GymSimpleResponse> results = gymService.search(authUser, null, null, null, 1 ,10 ,100d);

        // then
        assertEquals(3, results.getTotalElements());
        assertEquals(gym1.getName(), results.getContent().get(0).getName());
        assertEquals(gym2.getName(), results.getContent().get(1).getName());
        assertEquals(gym3.getName(), results.getContent().get(2).getName());

    }


    @Test
    void search_name_success() {
        // given
        Gym gym1 = new Gym("공공체육관1", "공공체육관 설명1", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, GymType.PUBLIC, user);
        Gym gym2 = new Gym("공공체육관2", "공공체육관 설명2", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, GymType.PUBLIC, user);
        List<Gym> gymPage = Arrays.asList(gym1, gym2);
        when(gymRepository.search("공공", null, null, user.getLatitude(), user.getLongitude(), null)).thenReturn(gymPage);
        when(userRepository.findByIdAndDeletedAtIsNullOrThrow(authUser.getUserId())).thenReturn(user);
        when(distanceCalculator.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(BigDecimal.valueOf(0));

        // when
        Page<GymSimpleResponse> results = gymService.search(authUser, "공공", null, null, 1 ,10 ,null);

        // then
        assertEquals(2, results.getTotalElements());
        assertEquals(gym1.getName(), results.getContent().get(0).getName());
        assertEquals(gym2.getName(), results.getContent().get(1).getName());
    }


    @Test
    void businessSearch_success() {
        // given
        Gym gym1 = new Gym("공공체육관1", "공공체육관 설명1", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, GymType.PUBLIC, user);
        Gym gym2 = new Gym("공공체육관2", "공공체육관 설명2", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, GymType.PUBLIC, user);
        User user2 = new User("유저2", "user2@email.com", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, "encodedPassword", Ranks.RANK_B, UserRole.BUSINESS);
        Gym gym3 = new Gym("공공체육관3", "공공체육관 설명3", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, GymType.PUBLIC, user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Gym> gymPage = new PageImpl<>(Arrays.asList(gym1, gym2), PageRequest.of(0, 10), 3);
        when(gymRepository.findByUserIdAndDeletedAtIsNull(authUser.getUserId(), pageable)).thenReturn(gymPage);

        // when
        Page<GymSimpleResponse> results = gymService.businessSearch(authUser, 1, 10);

        // then
        assertEquals(2, results.getTotalElements());
        assertEquals(gym1.getName(), results.getContent().get(0).getName());
        assertEquals(gym2.getName(), results.getContent().get(1).getName());
    }

    @Test
    void update_success() {
        // given
        Gym gym = new Gym("공공체육관", "공공체육관 설명", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, GymType.PUBLIC, user);
        when(gymRepository.findByIdAndDeletedAtIsNullOrThrow(1L)).thenReturn(gym);
        geoCodeDocument = new GeoCodeDocument();
        GeoCodeDocument.NestedAddress1 address = new GeoCodeDocument.NestedAddress1();
        ReflectionTestUtils.setField(address, "addressName", "서울특별시 종로구 세종로 1-1");
        ReflectionTestUtils.setField(geoCodeDocument, "regionAddress", address);
        GymUpdateRequest gymUpdateRequest = new GymUpdateRequest("수정체육관", "수정설명", "서울특별시 종로구 세종로 1-1");
        when(geoCodeService.getGeoCode(gymUpdateRequest.getUpdateAddress())).thenReturn(geoCodeDocument);
        // when
        GymUpdateResponse result = gymService.update(authUser, 1L, gymUpdateRequest);

        // then
        assertEquals(gymUpdateRequest.getUpdateName(), result.getName());
        assertEquals(gymUpdateRequest.getUpdateDescription(), result.getDescription());
        assertEquals(gymUpdateRequest.getUpdateAddress(), result.getRegionAddress());
    }


    @Test
    void update_fail_no_auth() {
        // given
        AuthUser authUser2 = new AuthUser(2L, "사업자2", "test2@gmail.com", UserRole.BUSINESS);
        Gym gym = new Gym("공공체육관2", "공공체육관 설명2", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, GymType.PUBLIC, user);
        GymUpdateRequest gymUpdateRequest = new GymUpdateRequest("수정체육관", "수정설명", "수정주소");
        when(gymRepository.findByIdAndDeletedAtIsNullOrThrow(1L)).thenReturn(gym);

        // when && then
        CustomException exception = assertThrows(CustomException.class, () -> {
            gymService.update(authUser2, 1L, gymUpdateRequest);
        });
    }


    @Test
    void delete_success() {
        // given
        Gym gym = new Gym("공공체육관2", "공공체육관 설명2", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, GymType.PUBLIC, user);
        when(gymRepository.findByIdAndDeletedAtIsNullOrThrow(1L)).thenReturn(gym);

        // when
        gymService.delete(authUser, 1L);

        // then
        assertNotNull(gym.getDeletedAt());

    }


    @Test
    void delete_fail_no_auth() {
        // given
        AuthUser authUser2 = new AuthUser(2L, "사업자2", "test2@gmail.com", UserRole.BUSINESS);
        Gym gym = new Gym("공공체육관2", "공공체육관 설명2", "서울 중구 태평로1가 31", "서울 중구 세종대로 110", 37.5663174209601, 126.977829174031, GymType.PUBLIC, user);
        when(gymRepository.findByIdAndDeletedAtIsNullOrThrow(1L)).thenReturn(gym);

        // when && then
        CustomException exception = assertThrows(CustomException.class, () -> {
            gymService.delete(authUser2, 1L);
        });
    }

}