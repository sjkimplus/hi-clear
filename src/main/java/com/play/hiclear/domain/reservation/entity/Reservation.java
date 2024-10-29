package com.play.hiclear.domain.reservation.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.court.entity.Court;
import com.play.hiclear.domain.reservation.enums.ReservationStatus;
import com.play.hiclear.domain.timeslot.entity.TimeSlot;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "reservations")
public class Reservation extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;                      // 예약한 사람

    @ManyToOne
    @JoinColumn(name="timeslot_id")
    private TimeSlot timeSlot;              // 코트 시간

    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;                    // 코트

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;       // 예약 상태

    public Reservation(User user, Court court, TimeSlot timeSlot, ReservationStatus status) {
        this.user = user;
        this.court = court;
        this.timeSlot = timeSlot;
        this.status = status;
    }

    // 예약 수정 메서드
    public void update(TimeSlot newTimeSlot, Court newCourt) {
        this.timeSlot = newTimeSlot;
        this.court = newCourt;
    }

    // 상태 업데이트 메서드
    public void updateStatus(ReservationStatus newStatus) {
        this.status = newStatus;
    }
}
