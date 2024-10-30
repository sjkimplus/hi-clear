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
        when(reservationService.create(authUser.getEmail(), request)).thenReturn(expectedResponse);

        // when
        List<ReservationSearchDetailResponse> actualResponse = reservationController.create(authUser, request);

        // then
        assertEquals(expectedResponse, actualResponse);
        verify(reservationService, times(1)).create(authUser.getEmail(), request);
    }

    @Test
    void get_success() {
        // given
        Long reservationId = 1L;
        ReservationSearchDetailResponse expectedResponse = new ReservationSearchDetailResponse();
        when(reservationService.get(reservationId, authUser.getEmail())).thenReturn(expectedResponse);

        // when
        ReservationSearchDetailResponse actualResponse = reservationController.get(reservationId, authUser);

        // then
        assertEquals(expectedResponse, actualResponse);
        verify(reservationService, times(1)).get(reservationId, authUser.getEmail());
    }

    @Test
    void search_success() {
        // given
        List<ReservationSearchResponse> expectedResponse = Collections.singletonList(new ReservationSearchResponse());
        when(reservationService.search(authUser.getEmail())).thenReturn(expectedResponse);

        // when
        List<ReservationSearchResponse> actualResponse = reservationController.search(authUser);

        // then
        assertEquals(expectedResponse, actualResponse);
        verify(reservationService, times(1)).search(authUser.getEmail());
    }

    @Test
    void update_Time_success() {
        // given
        Long reservationId = 1L;
        ReservationUpdateRequest request = new ReservationUpdateRequest();
        ReservationSearchDetailResponse expectedResponse = new ReservationSearchDetailResponse();
        when(reservationService.update(reservationId, authUser.getEmail(), request)).thenReturn(expectedResponse);

        // when
        ReservationSearchDetailResponse actualResponse = reservationController.update(reservationId, authUser, request);

        // then
        assertEquals(expectedResponse, actualResponse);
        verify(reservationService, times(1)).update(reservationId, authUser.getEmail(), request);
    }

    @Test
    void delete_success() {
        // given
        Long reservationId = 1L;

        // when
        ResponseEntity<Map<String, Object>> actualResponse = reservationController.delete(reservationId, authUser);

        // then
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals("예약이 성공적으로 취소되었습니다.", actualResponse.getBody().get("message"));
        verify(reservationService, times(1)).delete(reservationId, authUser.getEmail());
    }

    // 예약 상태 변경 성공 테스트 케이스
    @Test
    void changeReservation_success() {
        // given
        Long reservationId = 1L;
        ReservationChangeStatusRequest request = new ReservationChangeStatusRequest("ACCEPTED");
        Map<String, Object> expectedResponse = Map.of(
                "code", HttpStatus.OK.value(),
                "message", "예약 상태가 성공적으로 변경되었습니다.",
                "status", HttpStatus.OK.name()
        );

        // when
        ResponseEntity<Map<String, Object>> actualResponse = reservationController.change(reservationId, request, authUser);

        // then
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedResponse, actualResponse.getBody());
        verify(reservationService, times(1)).change(reservationId, authUser.getEmail(), request);
    }
}