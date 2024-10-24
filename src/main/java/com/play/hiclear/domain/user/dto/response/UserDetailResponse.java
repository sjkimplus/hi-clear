package com.play.hiclear.domain.user.dto.response;

import com.play.hiclear.common.enums.Ranks;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDetailResponse {

    private String nameEmail;
    private String region;
    private Ranks selfRank;
//    private Ranks reviewRank;
//    private Long stringTention;
}
