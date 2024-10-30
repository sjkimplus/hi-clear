package com.play.hiclear.domain.user.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import com.play.hiclear.domain.participant.entity.Participant;
import com.play.hiclear.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ranks selfRank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @OneToMany(mappedBy = "owner")
    private List<Club> clubs = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ClubMember> clubMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Participant> participants = new ArrayList<>();

    public User(String name, String email, String region, Ranks selfRank, UserRole userRole){
        this.name = name;
        this.email = email;
        this.region = region;
        this.selfRank = selfRank;
        this.userRole = userRole;
    }
    public User(String name, String email, String region, String encodePassword, Ranks selfRank, UserRole role) {
        this.name = name;
        this.email = email;
        this.region = region;
        this.password = encodePassword;
        this.selfRank = selfRank;
        this.userRole = role;
    }

    public User(Long id, String name, String email, String region, Ranks selfRank, UserRole userRole) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.region = region;
        this.selfRank = selfRank;
        this.userRole = userRole;
    }

    public void update(String region, String selfRank) {
        if(region != null){
            this.region = region;
        }
        if(selfRank != null){
            this.selfRank = Ranks.of(selfRank);
        }
    }
}
