package com.play.hiclear.domain.reservation.enums;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;

public enum ReservationStatus {
    PENDING,  // 대기 중
    ACCEPTED, // 승인됨
    REJECTED,  // 거절됨
    CANCELED;    // 취소됨


    // status가 수락/거절인지 확인(사장님이 예약을 수락/거절 할 때)
    public static ReservationStatus of(String status) {
        if (!status.equalsIgnoreCase("ACCEPTED") && !status.equalsIgnoreCase("REJECTED")) {
            throw new CustomException(ErrorCode.RESERVATION_BAD_REQUEST_ROLE);
        }

        return ReservationStatus.valueOf(status.toUpperCase());
    }
}