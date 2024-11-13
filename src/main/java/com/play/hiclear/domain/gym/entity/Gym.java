package com.play.hiclear.domain.gym.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.gym.enums.GymType;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "gyms", indexes = {@Index(name = "point_idx", columnList = "location")})
public class Gym extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String regionAddress;

    private String roadAddress;

    @Column(columnDefinition = "POINT", nullable = false)
    private Point location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GymType gymType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "gym", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Court> courts = new ArrayList<>();


    public Gym(String name, String description, String regionAddress, String roadAddress, Point location, GymType gymType, User user) {
        this.name = name;
        this.description = description;
        this.regionAddress = regionAddress;
        this.roadAddress = roadAddress;
        this.location = location;
        this.gymType = gymType;
        this.user = user;

    }

    // Gym Update 메서드
    public void update(String updateName, String updateDescription, String regionAddress, String roadAddress, Point location) {
        if (updateName != null && !updateName.isEmpty()) {
            this.name = updateName;
        }
        if (updateDescription != null && !updateDescription.isEmpty()) {
            this.description = updateDescription;
        }
        if (regionAddress != null && !regionAddress.isEmpty()) {
            this.regionAddress = regionAddress;
            this.roadAddress = roadAddress;
            this.location= location;
        }
    }
}
