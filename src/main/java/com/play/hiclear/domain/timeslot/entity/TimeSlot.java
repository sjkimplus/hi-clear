package com.play.hiclear.domain.timeslot.entity;

import com.play.hiclear.domain.court.entity.Court;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "timeslots")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    private Long gymId;

    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;

    public TimeSlot(LocalTime startTime, Long gymId, Court court) {
        this.startTime = startTime;
        this.endTime = startTime.plusHours(1);
        this.gymId = gymId;
        this.court = court;
    }
}
