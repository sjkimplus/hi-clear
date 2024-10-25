package com.play.hiclear.domain.gym.dto.response;

import com.play.hiclear.domain.gym.enums.GymType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GymSaveResponse {

    private Long id;
    private String name;
    private String description;
    private String address;
    private GymType gymType;
}
