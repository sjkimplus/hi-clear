package com.play.hiclear.domain.court.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.gym.entity.Gym;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courts")
public class Court extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long courtNum;

    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    public Court(long courtNum, int price, Gym gym) {
        this.courtNum = courtNum;
        this.price = price;
        this.gym = gym;
    }

    public void update(int price) {
        this.price = price;
    }
}
