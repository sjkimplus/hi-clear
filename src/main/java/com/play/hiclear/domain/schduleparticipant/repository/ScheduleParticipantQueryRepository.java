package com.play.hiclear.domain.schduleparticipant.repository;

import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.user.entity.User;

public interface ScheduleParticipantQueryRepository {
    boolean existsByScheduleAndUser(Schedule savedSchedule, User participantUser);
}
