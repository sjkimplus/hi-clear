package com.play.hiclear.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ex)
    /*AUTH_USER_EXISTING(HttpStatus.CONFLICT, "해당 이메일으로 가입된 유저가 이미 존재합니다."),
    AUTH_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "가입되지 않은 유저입니다."),
    AUTH_BAD_REQUEST_PASSWORD(HttpStatus.BAD_REQUEST, "입력하신 비밀번호가 올바르지 않습니다. 비밀번호를 다시 확인하고 입력해 주세요."),
    AUTH_USER_DELETED(HttpStatus.NOT_FOUND, "탈퇴한 유저 입니다."),*/


    // Auth
    AUTH_USER_EXISTING(HttpStatus.CONFLICT, "해당 이메일으로 가입된 유저가 이미 존재합니다."),
    AUTH_USER_DELETED(HttpStatus.NOT_FOUND, "탈퇴한 유저 입니다."),
    AUTH_BAD_REQUEST_PASSWORD(HttpStatus.BAD_REQUEST, "입력하신 비밀번호가 올바르지 않습니다. 비밀번호를 다시 확인하고 입력해 주세요."),






    // User





    // Schedule
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 모임 일정을 찾을 수 없습니다."),
    SCHEDULE_PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 모임에 참가자가 아닙니다."),
    SCHEDULE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 모임 일정입니다."),
    SCHEDULE_FORBIDDEN(HttpStatus.FORBIDDEN, "이 모임일정을 조회할 권한이 없습니다."),
    SCHEDULE_TIME_CONFLICT(HttpStatus.CONFLICT, "이미 다른 모임이 있는 시간입니다."),

    // Reservation
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당예약을 찾을 수 없거나, 해당예약을 생성한 사용자가 아닙니다."),
    RESERVATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 예약입니다."),
    RESERVATION_LIST_EMPTY(HttpStatus.NOT_FOUND, "예약 목록이 비어있습니다."),
    RESERVATION_MODIFICATION_NOT_ALLOWED(HttpStatus.FORBIDDEN, "수락, 거절, 취소된 예약은 수정할 수 없습니다."),
    TIME_SLOT_ALREADY_RESERVED(HttpStatus.CONFLICT, "해당 시간 슬롯은 이미 예약되었습니다."),
    TIME_SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 코트 시간을 찾을 수 없습니다."),



    // Meeting
    MEETING_CREATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "번개일정 생성 실패하였 습니다."),



    // 기본 코드
    NO_AUTHORITY(HttpStatus.FORBIDDEN, "%s 대한권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "%s 찾지못했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message){
        this.status = httpStatus;
        this.message = message;
    }

    public String customMessage(String detail) {
        return String.format(message, detail);
    }
}
