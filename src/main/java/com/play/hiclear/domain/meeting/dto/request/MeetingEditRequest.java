package com.play.hiclear.domain.meeting.dto.request;

import com.play.hiclear.common.enums.Ranks;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MeetingEditRequest {
    private String title;

    private String address;

    private String content;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Ranks ranks;

    private int groupSize;
}
