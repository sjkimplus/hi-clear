package com.play.hiclear.domain.schduleparticipant.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "schduleparticipants")
public class ScheduleParticipant extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;                  // 모임

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;                  // 참여자

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;      // 참가한 모임 일정
}
