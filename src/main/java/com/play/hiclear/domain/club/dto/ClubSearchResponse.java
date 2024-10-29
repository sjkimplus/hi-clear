package com.play.hiclear.domain.club.dto;

import com.play.hiclear.domain.club.entity.Club;
import lombok.Getter;

@Getter
public class ClubSearchResponse {

    private final String clubname;
    private final String intro;

    public ClubSearchResponse(Club club){
        this.clubname = club.getClubname();
        this.intro = club.getIntro();
    }
}
