package com.play.hiclear.domain.user.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.clubmember.entity.ClubMember;
import com.play.hiclear.domain.gym.entity.Gym;
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

    private String address;

    private String imgUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ranks selfRank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @OneToMany(mappedBy = "user")
    private List<Gym> gyms = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Club> clubs = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ClubMember> clubMembers = new ArrayList<>();

    public User(String name, String email, String address, Ranks selfRank, UserRole userRole){
        this.name = name;
        this.email = email;
        this.address = address;
        this.selfRank = selfRank;
        this.userRole = userRole;
    }
    public User(String name, String email, String address, String encodePassword, Ranks selfRank, UserRole role) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.password = encodePassword;
        this.selfRank = selfRank;
        this.userRole = role;
    }

    public User(Long id, String name, String email, String address, Ranks selfRank, UserRole userRole) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.selfRank = selfRank;
        this.userRole = userRole;
    }

    public void update(String address, String selfRank) {
        if(address != null){
            this.address = address;
        }
        if(selfRank != null){
            this.selfRank = Ranks.of(selfRank);
        }
    }

    public void updateImage(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
