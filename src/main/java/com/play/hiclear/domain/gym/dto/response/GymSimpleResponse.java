package com.play.hiclear.domain.gym.dto.response;

import lombok.Getter;

@Getter
public class GymSimpleResponse {

    private final String name;
    private final String regionAddress;
    private Double distance;

    public GymSimpleResponse(String name, String regionAddress, Double distance) {
        this.name = name;
        this.regionAddress = regionAddress;
        this.distance = distance;
    }

    public GymSimpleResponse(String name, String regionAddress) {
        this.name = name;
        this.regionAddress = regionAddress;
    }
}
