package com.play.hiclear.domain.club.dto.response;

import com.play.hiclear.domain.clubmember.entity.ClubMember;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClubMemberDtoResponse {

    private final String username;
    private final String email;

    public static ClubMemberDtoResponse entityToDto(ClubMember clubMember) {
        return new ClubMemberDtoResponse(
                clubMember.getUser().getName(),
                clubMember.getUser().getEmail()
        );
    }
}
