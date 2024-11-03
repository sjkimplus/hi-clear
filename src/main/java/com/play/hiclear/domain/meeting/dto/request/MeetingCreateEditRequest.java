package com.play.hiclear.domain.meeting.dto.request;

import com.play.hiclear.common.enums.Ranks;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MeetingCreateEditRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String region;
    @NotBlank
    private String content;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
    @NotNull
    private Ranks ranks;
    @NotNull
    @Min(value=4, message = "번개 최소모집인원은 4명 입니다.")
    private int groupSize;

    public MeetingCreateEditRequest(String title, String region, String content, LocalDateTime now, LocalDateTime futureTime, Ranks ranks, int i) {
        this.title = title;
        this.region = region;
        this.content = content;
        this.startTime = now;
        this.endTime = futureTime;
        this.ranks = ranks;
        this.groupSize = i;
    }
}
