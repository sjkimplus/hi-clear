package com.play.hiclear.domain.club.dto;

import com.play.hiclear.domain.clubmember.entity.ClubMember;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClubMemberDto {

    private final String username;
    private final String email;

    public static ClubMemberDto entityToDto(ClubMember clubMember) {
        return new ClubMemberDto(
                clubMember.getUser().getName(),
                clubMember.getUser().getEmail()
        );
    }
}
