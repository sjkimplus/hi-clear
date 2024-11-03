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
    PARTICIPANT_JOIN("모임 가입을 완료했습니다"),

    // MEETING
    MEETING_FINISHED("번개가 완료처리 되었습니다"),


    // 기본 코드
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
