package com.play.hiclear.domain.club.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.club.dto.ClubUpdateRequest;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Club extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
    private String clubname;
    private Integer clubSize;
    private String intro;
    private String region;
    private String password;

    @OneToMany(mappedBy = "club")
    private List<ClubMember> clubMembers = new ArrayList<>();

    public void updateClub(ClubUpdateRequest clubUpdateRequest) {
        this.clubname = clubUpdateRequest.getClubname();
        this.clubSize = clubUpdateRequest.getClubSize();
        this.intro = clubUpdateRequest.getIntro();
        this.region = clubUpdateRequest.getRegion();
    }
}
