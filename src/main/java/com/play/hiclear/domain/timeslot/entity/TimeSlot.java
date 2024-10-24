package com.play.hiclear.domain.timeslot.entity;

import com.play.hiclear.domain.court.entity.Court;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "timeslots")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;

    public TimeSlot(Long id, LocalDateTime startTime, LocalDateTime endTime, Court court) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.court = court;
    }
}
