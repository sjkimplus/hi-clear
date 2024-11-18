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

import static com.play.hiclear.common.enums.Ranks.RANK_A;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    // 예약 생성
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

    // 예약 조회(단건)
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

    // 예약 조회(다건)
    @Test
    void search_success() {
        // given
        int page = 1;
        int size = 10;
        Long courtId = null;
        ReservationSearchResponse expectedResponse = new ReservationSearchResponse();
        List<ReservationSearchResponse> expectedList = Collections.singletonList(expectedResponse);
        Page<ReservationSearchResponse> expectedPage = new PageImpl<>(expectedList, PageRequest.of(page - 1, size), expectedList.size());

        when(reservationService.search(authUser, page, size, courtId, null, null))
                .thenReturn(expectedPage);

        // when
        ResponseEntity<List<ReservationSearchResponse>> actualResponse = reservationController.search(authUser, page, size, courtId, null, null);

        // then
        assertEquals(ResponseEntity.ok(expectedList), actualResponse);
        verify(reservationService, times(1)).search(authUser, page, size, courtId, null, null);
    }

    // 예약 수정
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

    // 예약 삭제
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

    // 사장님이 예약 수락/거절
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