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
