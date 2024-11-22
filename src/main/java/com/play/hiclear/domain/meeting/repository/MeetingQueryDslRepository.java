package com.play.hiclear.domain.meeting.repository;

import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.meeting.dto.response.MeetingSearchResponse;
import com.play.hiclear.domain.meeting.enums.SortType;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetingQueryDslRepository {
    Page<MeetingSearchResponse> search(SortType sortType, Ranks ranks, int distance, User user, Pageable pageable);
}
