package com.play.hiclear.domain.user.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.user.dto.request.UserUpdateRequest;
import com.play.hiclear.domain.user.dto.response.UserDetailResponse;
import com.play.hiclear.domain.user.dto.response.UserSimpleResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private AuthUser authUser;

    @BeforeEach
    void setup() {
        authUser = new AuthUser(1L, "홍길동", "test1@gmail.com", UserRole.BUSINESS);
    }


    @Test
    void search_success() {
        // given
        List<User> userList = new ArrayList<>();
        User user1 = new User("홍길동", "test1@gmail.com", "경기도 화성시", "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        User user2 = new User("김스파", "test2@gmail.com", "경기도 안산시", "encodedPassword", Ranks.RANK_B, UserRole.BUSINESS);
        userList.add(user1);
        userList.add(user2);

        int page = 1;
        int size = 10;
        Page<User> userPage = new PageImpl<>(userList, PageRequest.of(page - 1, size), userList.size());
        when(userRepository.findAll(PageRequest.of(page - 1, size))).thenReturn(userPage);


        // when
        Page<UserSimpleResponse> result = userService.search(page, size);

        // then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("홍길동", result.getContent().get(0).getName());
        assertEquals(Ranks.RANK_A.name(), result.getContent().get(0).getSelfRank());
        assertEquals("경기도 화성시", result.getContent().get(0).getRegion());
        assertEquals("김스파", result.getContent().get(1).getName());
        assertEquals(Ranks.RANK_B.name(), result.getContent().get(1).getSelfRank());
        assertEquals("경기도 안산시", result.getContent().get(1).getRegion());
    }


    @Test
    void update_successs() {
        // given
        User user = new User("홍길동", "test1@gmail.com", "경기도 화성시", "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("서울특별시 강서구", "RANK_C");

        // when
        userService.update(authUser, userUpdateRequest);

        // then
        assertEquals("서울특별시 강서구", user.getRegion());
        assertEquals(Ranks.RANK_C, user.getSelfRank());
    }


    @Test
    void get_success() {
        // given
        User user = new User("홍길동", "test1@gmail.com", "경기도 화성시", "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserDetailResponse result = userService.get(authUser, 1L);

        // then
        assertNotNull(result);
        assertEquals("홍길동(test1@gmail.com)", result.getNameEmail());
        assertEquals("경기도 화성시", result.getRegion());
        assertEquals(Ranks.RANK_A, result.getSelfRank());
    }


}