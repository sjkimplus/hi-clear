package com.play.hiclear.domain.gym.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
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

import java.util.Arrays;
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

    private GymCreateRequest gymCreateRequest;
    private AuthUser authUser;
    private User user;

    @BeforeEach
    void setup() {
        authUser = new AuthUser(1L, "사업자1", "test1@gmail.com", UserRole.BUSINESS);
        gymCreateRequest = new GymCreateRequest("공공체육관1", "서울특별시", "공공체육관1 설명", "PUBLIC");
        user = new User(authUser.getName(), authUser.getEmail(), "서울특별시", "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        ReflectionTestUtils.setField(user, "id", 1L);
    }


    @Test
    void create_success() {
        // given
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        // when
        GymCreateResponse result = gymService.create(authUser, gymCreateRequest);

        // then
        verify(gymRepository, times(1)).save(any(Gym.class));
        assertEquals(gymCreateRequest.getName(), result.getName());
        assertEquals(gymCreateRequest.getAddress(), result.getAddress());
        assertEquals(gymCreateRequest.getDescription(), result.getDescription());
        assertEquals(gymCreateRequest.getGymType(), result.getGymType().name());
    }


    @Test
    void search_success() {
        // given
        Gym gym1 = new Gym("공공체육관1", "공공체육관 설명1", "서울특별시", GymType.PUBLIC, user);
        Gym gym2 = new Gym("공공체육관2", "공공체육관 설명2", "서울특별시", GymType.PUBLIC, user);
        Gym gym3 = new Gym("사설체육관1", "사설체육관 설명1", "서울특별시", GymType.PRIVATE, user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Gym> gymPage = new PageImpl<>(Arrays.asList(gym1, gym2, gym3), PageRequest.of(0, 10), 3);
        when(gymRepository.searchGyms(null, null, null, pageable)).thenReturn(gymPage);

        // when
        Page<GymSimpleResponse> results = gymService.search(1, 10, null, null, null);


        // then
        assertEquals(3, results.getTotalElements());
        assertEquals(gym1.getName(), results.getContent().get(0).getName());
        assertEquals(gym2.getName(), results.getContent().get(1).getName());
        assertEquals(gym3.getName(), results.getContent().get(2).getName());

    }


    @Test
    void search_name_success() {
        // given
        Gym gym1 = new Gym("공공체육관1", "공공체육관 설명1", "서울특별시", GymType.PUBLIC, user);
        Gym gym2 = new Gym("공공체육관2", "공공체육관 설명2", "서울특별시", GymType.PUBLIC, user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Gym> gymPage = new PageImpl<>(Arrays.asList(gym1, gym2), PageRequest.of(0, 10), 3);
        when(gymRepository.searchGyms("공공", null, null, pageable)).thenReturn(gymPage);

        // when
        Page<GymSimpleResponse> results = gymService.search(1, 10, "공공", null, null);


        // then
        assertEquals(2, results.getTotalElements());
        assertEquals(gym1.getName(), results.getContent().get(0).getName());
        assertEquals(gym2.getName(), results.getContent().get(1).getName());
    }


    @Test
    void businessSearch_success() {
        // given
        Gym gym1 = new Gym("공공체육관1", "공공체육관 설명1", "서울특별시", GymType.PUBLIC, user);
        Gym gym2 = new Gym("공공체육관2", "공공체육관 설명2", "서울특별시", GymType.PUBLIC, user);
        User user2 = new User("유저2", "user2@email.com", "서울특별시", "encodedPassword", Ranks.RANK_B, UserRole.BUSINESS);
        Gym gym3 = new Gym("사설체육관1", "사설체육관 설명1", "서울특별시", GymType.PRIVATE, user2);
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
        Gym gym = new Gym("공공체육관1", "공공체육관 설명1", "서울특별시", GymType.PUBLIC, user);
        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));
        GymUpdateRequest gymUpdateRequest = new GymUpdateRequest("수정체육관", "수정설명", "수정주소");
        // when
        GymUpdateResponse result = gymService.update(authUser, 1L, gymUpdateRequest);

        // then
        assertEquals(gymUpdateRequest.getUpdateName(), result.getName());
        assertEquals(gymUpdateRequest.getUpdateDescription(), result.getDescription());
        assertEquals(gymUpdateRequest.getUpdateAddress(), result.getAddress());
    }


    @Test
    void update_fail_no_auth() {
        // given
        AuthUser authUser2 = new AuthUser(2L, "사업자2", "test2@gmail.com", UserRole.BUSINESS);
        Gym gym = new Gym("공공체육관1", "공공체육관 설명1", "서울특별시", GymType.PUBLIC, user);
        GymUpdateRequest gymUpdateRequest = new GymUpdateRequest("수정체육관", "수정설명", "수정주소");
        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        // when && then
        CustomException exception = assertThrows(CustomException.class, () -> {
            gymService.update(authUser2, 1L, gymUpdateRequest);
        });
    }


    @Test
    void delete_success() {
        // given
        Gym gym = new Gym("공공체육관1", "공공체육관 설명1", "서울특별시", GymType.PUBLIC, user);
        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        // when
        gymService.delete(authUser, 1L);

        // then
        assertNotNull(gym.getDeletedAt());

    }


    @Test
    void delete_fail_no_auth() {
        // given
        AuthUser authUser2 = new AuthUser(2L, "사업자2", "test2@gmail.com", UserRole.BUSINESS);
        Gym gym = new Gym("공공체육관1", "공공체육관 설명1", "서울특별시", GymType.PUBLIC, user);
        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        // when && then
        CustomException exception = assertThrows(CustomException.class, () -> {
            gymService.delete(authUser2, 1L);
        });
    }

}