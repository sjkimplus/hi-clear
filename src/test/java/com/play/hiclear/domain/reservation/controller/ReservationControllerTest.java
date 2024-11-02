package com.play.hiclear.domain.reservation.controller;

import com.play.hiclear.domain.auth.entity.AuthUser;
import com.play.hiclear.domain.reservation.dto.request.ReservationChangeStatusRequest;
import com.play.hiclear.domain.reservation.dto.request.ReservationRequest;
import com.play.hiclear.domain.reservation.dto.request.ReservationUpdateRequest;
import com.play.hiclear.domain.reservation.dto.response.ReservationSearchDetailResponse;
import com.play.hiclear.domain.reservation.dto.response.ReservationSearchResponse;
import com.play.hiclear.domain.reservation.service.ReservationService;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.play.hiclear.common.enums.Ranks.RANK_A;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationControllerTest {

    @InjectMocks
    private ReservationController reservationController;

    @Mock
    private ReservationService reservationService;

    private AuthUser authUser;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("John Doe", "john@example.com", "전라북도 익산시", RANK_A, UserRole.BUSINESS);

        authUser = new AuthUser(1L, "John Doe", "john@example.com", UserRole.BUSINESS);
    }

    @Test
    void create_success() {
        // given
        ReservationRequest request = new ReservationRequest();
        List<ReservationSearchDetailResponse> expectedResponse = Collections.singletonList(new ReservationSearchDetailResponse());
        when(reservationService.create(authUser, request)).thenReturn(expectedResponse);

        // when
        List<ReservationSearchDetailResponse> actualResponse = reservationController.create(authUser, request);

        // then
        assertEquals(expectedResponse, actualResponse);
        verify(reservationService, times(1)).create(authUser, request);
    }

    @Test
    void get_success() {
        // given
        Long reservationId = 1L;
        ReservationSearchDetailResponse expectedResponse = new ReservationSearchDetailResponse();
        when(reservationService.get(reservationId, authUser)).thenReturn(expectedResponse);

        // when
        ReservationSearchDetailResponse actualResponse = reservationController.get(reservationId, authUser);

        // then
        assertEquals(expectedResponse, actualResponse);
        verify(reservationService, times(1)).get(reservationId, authUser);
    }

    @Test
    void search_success() {
        // given
        int page = 1;
        int size = 10;
        Long courtId = null;
        ReservationSearchResponse expectedResponse = new ReservationSearchResponse(); // 여기에 실제 응답 객체를 생성하세요.
        Page<ReservationSearchResponse> expectedPage = new PageImpl<>(Collections.singletonList(expectedResponse), PageRequest.of(page - 1, size), 1);

        when(reservationService.search(authUser, page, size, courtId, null, null)).thenReturn(expectedPage);

        // when
        Page<ReservationSearchResponse> actualResponse = reservationController.search(authUser, page, size, courtId, null, null);

        // then
        assertEquals(expectedPage, actualResponse);
        verify(reservationService, times(1)).search(authUser, page, size, courtId, null, null);
    }


    @Test
    void update_success() {
        // given
        Long reservationId = 1L;
        ReservationUpdateRequest request = new ReservationUpdateRequest();
        ReservationSearchDetailResponse expectedResponse = new ReservationSearchDetailResponse();
        when(reservationService.update(reservationId, authUser, request)).thenReturn(expectedResponse);

        // when
        ReservationSearchDetailResponse actualResponse = reservationController.update(reservationId, authUser, request);

        // then
        assertEquals(expectedResponse, actualResponse);
        verify(reservationService, times(1)).update(reservationId, authUser, request);
    }

    @Test
    void delete_success() {
        // given
        Long reservationId = 1L;

        // when
        ResponseEntity<String> actualResponse = reservationController.delete(reservationId, authUser);

        // then
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals("Reservation을(를) 삭제했습니다.", actualResponse.getBody());
        verify(reservationService, times(1)).delete(reservationId, authUser);
    }

    @Test
    void change_success() {
        // given
        Long reservationId = 1L;
        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest("ACCEPTED");
        String expectedResponse = "사장님이 예약을 수락했습니다.";

        // when
        ResponseEntity<String> actualResponse = reservationController.change(reservationId, request, authUser);

        // then
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedResponse, actualResponse.getBody()); // 수정된 응답 비교
        verify(reservationService, times(1)).change(reservationId, authUser, request);
    }
}