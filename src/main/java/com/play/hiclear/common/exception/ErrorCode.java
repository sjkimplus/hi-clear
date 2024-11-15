package com.play.hiclear.common.exception;

import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.user.enums.UserRole;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

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
    AUTH_BAD_REQUEST_ROLE(HttpStatus.BAD_REQUEST,
            String.format("UserRole 입력이 올바르지 않습니다. 가능한 값: %s",
                    Arrays.toString(UserRole.values()))),


    // Gym
    GYM_BAD_REQUEST_TYPE(HttpStatus.BAD_REQUEST,
            String.format("GymType 입력이 올바르지 않습니다. 가능한 값: %s",
                    Arrays.toString(GymType.values()))),


    // Timeslot
    TIME_SLOT_ALREADY_EXIST(HttpStatus.ALREADY_REPORTED, "코트에 이미 해당 시간대가 등록돼있습니다."),



    // Court
    COURT_ALREADY_EXIST(HttpStatus.ALREADY_REPORTED, "해당 번호의 코트가 이미 존재합니다."),





    // Schedule
    SCHEDULE_NOT_A_CLUB_MEMBER(HttpStatus.NOT_FOUND, "해당 모임에 멤버가 아닙니다."),
    SCHEDULE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 모임 일정입니다."),
    SCHEDULE_TIME_CONFLICT(HttpStatus.CONFLICT, "이미 다른 모임이 있는 시간입니다."),
    DUPLICATE_SCHEDULE(HttpStatus.CONFLICT, "같은 클럽에서 동일한 시작 시간과 제목을 가진 일정이 이미 존재합니다."),
    INVALID_SCHEDULE_TIME(HttpStatus.CONFLICT, "모임 일정의 시간이 맞지 않습니다."),
    ALREADY_DELETED(HttpStatus.CONFLICT, "이미 삭제된 모임일정입니다."),
    PARTICIPANT_NOT_FOUND(HttpStatus.CONFLICT, "모임 일정에 참여한 참가자가 아닙니다."),
    PARTICIPANT_ALREAY_EXISTED(HttpStatus.CONFLICT, "모임 일정에 이미 참가했습니다."),

    // Reservation
    TIME_SLOT_ALREADY_RESERVED(HttpStatus.CONFLICT, "해당 시간 슬롯은 이미 예약되었습니다."),
    RESERVATION_CANT_CANCELED(HttpStatus.CONFLICT, "예약을 취소할 수 없습니다."),
    TIME_IS_ALREAY_PASSED(HttpStatus.CONFLICT, "현재 시간에서 예약 시작까지 24시간 이상 남아 있어야만 취소가 가능합니다."),
    RESERVATION_CANT_ACCEPTED(HttpStatus.CONFLICT, "예약은 이미 거절/취소 되었습니다."),
    INVALID_DATE(HttpStatus.CONFLICT, "예약 날짜는 현재 시간 이후여야 합니다."),
    INVALID_TIME_SLOT(HttpStatus.CONFLICT, "선택한 시간 슬롯이 해당 코트와 일치하지 않습니다."),
    RESERVATION_BAD_REQUEST_ROLE(HttpStatus.BAD_REQUEST,
            String.format("ReservationStatus 입력이 올바르지 않습니다. 가능한 값: %s",
                    Arrays.toString(new String[]{"ACCEPTED", "REJECTED"}))),



    // Meeting
    TOO_SOON(HttpStatus.BAD_REQUEST, "번개일정 종료전에 번개완료를 할 수는 없습니다"),
    TOO_LATE(HttpStatus.BAD_REQUEST, "번개일정 참여하기에는 너무 늦었습니다"),
    FULL(HttpStatus.BAD_REQUEST, "번개일정 참여자가 모두 모집 되었습니다"),
    INVALID_TIME(HttpStatus.BAD_REQUEST, "시작시간은 현재시간 이후, 운동시간은 총 1시간이여야 합니다."),
    INVALID_DISTANCE(HttpStatus.BAD_REQUEST, "허용된 값은 [5, 10, 50, 100]km 입니다."),


    // ClubMember
    CLUBMEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 멤버입니다."),
    CLUBMEMBER_OVER(HttpStatus.CONFLICT, "정원 초과입니다"),
    CLUBMEMBER_NOT_EXPEL_ONESELF(HttpStatus.CONFLICT, "자기 자신을 추방할 수 없습니다"),
    CLUBMEMBER_ADMIN_NOT_WITHDRAW(HttpStatus.CONFLICT, "자기 자신을 추방할 수 없습니다"),
    CLUBMEMBER_ADMIN_ONLY_ONE(HttpStatus.CONFLICT, "모임장은 한 명만 존재할 수 있습니다."),



    // Participant
    DUPLICATE_PARTICIPANT(HttpStatus.CONFLICT, "이미 신청이 되어 있습니다."),
    TOO_LATE_TO_CANCEL(HttpStatus.BAD_REQUEST, "번개를 취소하기에는 너무 늦었습니다."),
    WRONG_STATUS(HttpStatus.BAD_REQUEST, "번개 참가자의 상태를 수정할 수 없습니다."),
    WRONG_MATCH(HttpStatus.BAD_REQUEST, "번개 번호 또는 참가자 번호가 유효하지 않습니다."),

    // Review
    REVIEW_MEETING_USER(HttpStatus.BAD_REQUEST, "해당 유저가 미팅에 속해있지 않습니다."),
    REVIEW_MISS_SCORE(HttpStatus.BAD_REQUEST, "잘못된 점수입니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 작성한 리뷰입니다."),
    LOCK_ACQUISITION_FAILED(HttpStatus.BAD_REQUEST, "락을 얻는데 실패하였습니다."),


    // Notification


    // 기본 코드
    DUMMY_ALREADY_EXIST(HttpStatus.ALREADY_REPORTED, "이미 데이터가 존재합니다."),
    ADDRESS_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 주소 입니다."),
    IMAGE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일입니다."),
    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 중 오류가 발생했습니다." ),
    IMAGE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패햇습니다." ),
    NO_AUTHORITY(HttpStatus.FORBIDDEN, "%s에 대한권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "%s을(를) 찾지못했습니다.");

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
