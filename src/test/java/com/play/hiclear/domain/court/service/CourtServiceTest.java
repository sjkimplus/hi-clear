package com.play.hiclear.domain.court.service;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.court.dto.request.CourtCreateRequest;
import com.play.hiclear.domain.court.dto.response.CourtCreateResponse;
import com.play.hiclear.domain.court.dto.response.CourtSearchResponse;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.court.repository.CourtRepository;
import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.gym.repository.GymRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourtServiceTest {

    @InjectMocks
    CourtService courtService;

    @Mock
    CourtRepository courtRepository;

    @Mock
    GymRepository gymRepository;

    private Gym gym;
    private AuthUser authUser;
    private User user;

    @BeforeEach
    void setup() {
        authUser = new AuthUser(1L, "사업자1", "test1@gmail.com", UserRole.BUSINESS);
        user = new User(authUser.getName(), authUser.getEmail(), "서울특별시", "encodedPassword", Ranks.RANK_A, UserRole.BUSINESS);
        ReflectionTestUtils.setField(user, "id", 1L);
        gym = new Gym("공공체육관1", "공공체육관 설명1", "서울특별시", GymType.PUBLIC, user);
        ReflectionTestUtils.setField(gym, "id", 1L);
        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));
    }


    @Test
    void create_success() {
        // given
        CourtCreateRequest courtCreateRequest = new CourtCreateRequest(10000);

        // when
        CourtCreateResponse result =courtService.create(authUser, 1L, courtCreateRequest);

        // then
        assertEquals(1, result.getCourtNum());
        assertEquals(courtCreateRequest.getPrice(), result.getPrice());
    }


    @Test
    void search_success() {
        // given
        List<Court> courtList = new ArrayList<>();
        courtList.add(new Court(1L, 10000, gym));
        courtList.add(new Court(2L, 15000, gym));
        courtList.add(new Court(3L, 20000, gym));
        Long gymId = 1L;
        when(courtRepository.findAllByGymId(gymId)).thenReturn(courtList);

        // when
        List<CourtSearchResponse> results = courtService.search(authUser, gymId);

        // then
        assertEquals(3, results.size());
        assertEquals(10000, results.get(0).getPrice());
        assertEquals(15000, results.get(1).getPrice());
        assertEquals(20000, results.get(2).getPrice());
    }

    @Test
    void update_success() {
        // given
        Court court = new Court(1L, 10000, gym);
        CourtCreateRequest courtUpdateRequest = new CourtCreateRequest(20000);
        when(courtRepository.findByCourtNumAndGymId(1L, 1L)).thenReturn(Optional.of(court));

        // when
        CourtCreateResponse result = courtService.update(authUser, 1L, 1L, courtUpdateRequest);

        // then
        assertEquals(courtUpdateRequest.getPrice(), result.getPrice());
    }

    @Test
    void delete_success() {
        // given
        Court court = new Court(1L, 10000, gym);
        CourtCreateRequest courtUpdateRequest = new CourtCreateRequest(20000);
        when(courtRepository.findByCourtNumAndGymId(1L, 1L)).thenReturn(Optional.of(court));

        // when
        courtService.delete(authUser, 1L, 1L);

        // then
        verify(courtRepository).delete(court);
        when(courtRepository.findByCourtNumAndGymId(1L, 1L)).thenReturn(Optional.empty());
        Optional<Court> result = courtRepository.findByCourtNumAndGymId(1L, 1L);
        assertTrue(result.isEmpty());
    }
}