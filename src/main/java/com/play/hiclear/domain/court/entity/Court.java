package com.play.hiclear.domain.court.entity;

import com.play.hiclear.domain.gym.entity.Gym;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "courts")
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long courtNum;

    private int price;

    private Boolean courtStatus = true; // 코트 활성화 여부(OPEN, CLOSE)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    public Court(Long courtNum, int price, Gym gym){
        this.courtNum = courtNum;
        this.price = price;
        this.gym = gym;
    }

    public void update(int price) {
        this.price = price;
    }
}
