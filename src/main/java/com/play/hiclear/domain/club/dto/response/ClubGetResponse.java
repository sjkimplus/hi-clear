package com.play.hiclear.domain.club.dto.response;

import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import lombok.Getter;

import java.util.List;

@Getter
public class ClubGetResponse {

    private final String clubname;
    private final Integer clubSize;
    private final String intro;
    private final String region;
    private final List<ClubMemberDtoResponse> members;

    public ClubGetResponse(Club club) {
        this.clubname = club.getClubname();
        this.clubSize = club.getClubSize();
        this.intro = club.getIntro();
        this.region = club.getRegion();
        this.members = memberResponseList(club.getClubMembers());
    }

    private List<ClubMemberDtoResponse> memberResponseList(List<ClubMember> memberList) {
        return memberList.stream().map(ClubMemberDtoResponse::entityToDto).toList();
    }
}
