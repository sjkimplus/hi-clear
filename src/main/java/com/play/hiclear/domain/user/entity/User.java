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
    private Ranks selectRank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @OneToMany(mappedBy = "user")
    private List<Club> clubs = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ClubMember> clubMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Participant> participants = new ArrayList<>();

    public User(Long id, String name, String email, String region, Ranks selectRank, UserRole userRole){
        this.id = id;
        this.name = name;
        this.email = email;
        this.region = region;
        this.selectRank = selectRank;
        this.userRole = userRole;
    }
    public User(String name, String email, String region, String encodePassword, Ranks selectRank, UserRole role) {
        this.name = name;
        this.email = email;
        this.region = region;
        this.password = encodePassword;
        this.selectRank = selectRank;
        this.userRole = role;
    }

   /* public static User fromAuthUser(AuthUser authUser){
        return new User(
                authUser.getUserId(),
                authUser.getName(),

        );
    }*/

}
