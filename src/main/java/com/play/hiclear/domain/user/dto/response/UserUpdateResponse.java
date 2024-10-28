package com.play.hiclear.domain.user.dto.response;

import com.play.hiclear.common.enums.Ranks;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateResponse {

    private final String region;
    private final Ranks selfRank;
}
