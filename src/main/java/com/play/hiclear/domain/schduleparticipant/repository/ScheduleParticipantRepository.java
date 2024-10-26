package com.play.hiclear.domain.schduleparticipant.repository;

import com.play.hiclear.domain.schduleparticipant.entity.ScheduleParticipant;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleParticipantRepository extends JpaRepository<ScheduleParticipant, Long>, ScheduleParticipantQueryRepository {
}
