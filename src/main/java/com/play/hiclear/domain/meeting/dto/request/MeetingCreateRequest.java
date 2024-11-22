package com.play.hiclear.domain.meeting.dto.request;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.participant.entity.Participant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class MeetingCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String address;

    @NotBlank
    private String content;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private Ranks ranks;

    @Min(value=4, message = "번개 최소모집인원은 4명 입니다.")
    private int groupSize;

}
