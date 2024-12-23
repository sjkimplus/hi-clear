package com.play.hiclear.common.message;

import lombok.Getter;

@Getter
public enum SuccessMessage {

    // CLUB
    CLUBMEMBER_JOIN("모임 가입을 완료했습니다"),
    CLUBMEMBER_WITHDRAW("모임 탈퇴를 완료했습니다."),
    CLUBMEMBER_EXPEL("모임에서 추방했습니다."),
    CLUBMEMBER_CHANGE_ROLE("권한을 변경했습니다."),

    // PARTIPCIPANT
    PARTICIPANT_JOIN("번개 신청을 완료했습니다"),

    // RESERVATION
    RESERVATION_ACCEPTED("사장님이 예약을 수락했습니다."),
    RESERVATION_REJECTED("사장님이 예약을 거절했습니다."),

    // SCHEDULE
    SCHEDULE_ADDED("모임 일정에 참가되었습니다."),
    SCHEDULE_DELETED("모임 일정에서 삭제되었습니다."),

    // MEETING
    MEETING_FINISHED("번개가 완료처리 되었습니다"),

    //NOTIFICATION
    NOTIFICATION_READ("알림을 읽었습니다."),
    NOTIFICATION_DELETED("알림을 삭제했습니다."),

    // 기본 코드
    POSTED("%s을(를) 등록했습니다."),
    CREATED("%s을(를) 생성했습니다."),
    MODIFIED("%s을(를) 수정했습니다."),
    DELETED("%s을(를) 삭제했습니다.");

    private String message;

    SuccessMessage(String message){
        this.message = message;
    };

    /**
     *
     * @param successMessage    정의된 enum
     * @return  정의된 메세지
     */
    public static String customMessage(SuccessMessage successMessage) {
        return String.format(successMessage.getMessage());
    }

    /**
     *
     * @param successMessage    정의된 enum
     * @param detailMessage 사용자 정의 세부메세지
     * @return String 정의된 메세지 + 세부 메세지
     */
    public static String customMessage(SuccessMessage successMessage, String detailMessage) {
        return String.format(successMessage.getMessage(), detailMessage);
    }
}
