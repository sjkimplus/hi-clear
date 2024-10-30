package com.play.hiclear.domain.meeting.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class MyMeetingResponses {
    List<MyMeetingResponse> list;

    public MyMeetingResponses(List<MyMeetingResponse> list) {
        this.list = list;
    }
}
