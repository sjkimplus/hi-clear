package com.play.hiclear.domain.user.dto.request;

import com.play.hiclear.common.enums.Ranks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    private String region;
    private String selfRank;
}
