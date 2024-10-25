package com.play.hiclear.domain.timeslot.entity;

import com.play.hiclear.domain.court.entity.Court;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "timeslots")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime  endTime;

    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;

    public TimeSlot(Long id, LocalTime  startTime, LocalTime  endTime, Court court) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.court = court;
    }
}
