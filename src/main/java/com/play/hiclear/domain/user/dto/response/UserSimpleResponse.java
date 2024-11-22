package com.play.hiclear.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSimpleResponse {

    private Long id;
    private String name;
    private String selfRank;
    private String regionAddress;
}
