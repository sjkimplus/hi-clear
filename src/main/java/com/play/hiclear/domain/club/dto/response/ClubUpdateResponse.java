package com.play.hiclear.domain.club.dto.response;

import com.play.hiclear.domain.club.entity.Club;
import lombok.Getter;

@Getter
public class ClubUpdateResponse {

    private final String clubname;
    private final Integer clubSize;
    private final String intro;
    private final String region;

    public ClubUpdateResponse(Club club) {
        this.clubname = club.getClubname();
        this.clubSize = club.getClubSize();
        this.intro = club.getIntro();
        this.region = club.getRegion();
    }
}
