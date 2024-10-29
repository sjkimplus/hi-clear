package com.play.hiclear.domain.schduleparticipant.repository;

import com.play.hiclear.domain.schduleparticipant.entity.ScheduleParticipant;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.user.entity.User;

import java.util.List;

public interface ScheduleParticipantQueryRepository {
    boolean existsByScheduleAndUser(Schedule savedSchedule, User participantUser);

    List<ScheduleParticipant> findBySchedule(Schedule schedule);
}
