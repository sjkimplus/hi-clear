package com.play.hiclear.domain.club.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.club.dto.request.ClubUpdateRequest;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

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
    @NotBlank
    private String clubname;
    @NotNull
    private Integer clubSize;
    private String intro;
    @NotBlank
    private String regionAddress;                       // 일반 주소
    private String roadAddress;                         // 도로명 주소
    @Column(columnDefinition = "POINT NOT NULL SRID 4326")
    private Point location;
    @NotBlank
    private String password;

    @OneToMany(mappedBy = "club")
    private List<ClubMember> clubMembers = new ArrayList<>();

    public Club(User owner, String clubname, Integer clubSize, String intro, String regionAddress, Point location, String roadAddress, String password) {
        this.owner = owner;
        this.clubname = clubname;
        this.clubSize = clubSize;
        this.intro = intro;
        this.regionAddress = regionAddress;
        this.roadAddress = roadAddress;
        this.location = location;
        this.password = password;
    }

    public void updateClub(ClubUpdateRequest clubUpdateRequest) {
        this.clubname = clubUpdateRequest.getClubname();
        this.clubSize = clubUpdateRequest.getClubSize();
        this.intro = clubUpdateRequest.getIntro();
        this.regionAddress = clubUpdateRequest.getRegion();
    }
}
