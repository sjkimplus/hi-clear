package com.play.hiclear.domain.gym.dto.response;

import lombok.Getter;

@Getter
public class GymSimpleResponse {

    private final String name;
    private final String regionAddress;
    private double distance;

    public GymSimpleResponse(String name, String regionAddress, double distance) {
        this.name = name;
        this.regionAddress = regionAddress;
        this.distance = distance;
    }

    public GymSimpleResponse(String name, String regionAddress) {
        this.name = name;
        this.regionAddress = regionAddress;
    }
}
