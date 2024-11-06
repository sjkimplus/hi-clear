package com.play.hiclear.domain.gym.dto.response;

import com.play.hiclear.domain.gym.entity.Gym;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.schedule.dto.response.ClubScheduleResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class GymDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String regionAddress;
    private String roadAddress;
    private GymType gymType;
    private List<String> clubNames = null;
    private List<ClubScheduleResponse> todaySchedule = null;

    public GymDetailResponse(Gym gym, List<String> clubNames, List<ClubScheduleResponse> todaySchedule) {
        this.id = gym.getId();
        this.name = gym.getName();
        this.description = gym.getDescription();
        this.regionAddress = gym.getRegionAddress();
        this.gymType = gym.getGymType();
        this.clubNames = clubNames;
        this.todaySchedule = todaySchedule;
    }

    public GymDetailResponse(Gym gym) {
        this.id = gym.getId();
        this.name = gym.getName();
        this.description = gym.getDescription();
        this.regionAddress = gym.getRegionAddress();
        this.roadAddress = gym.getRoadAddress();
        this.gymType = gym.getGymType();
    }
}
