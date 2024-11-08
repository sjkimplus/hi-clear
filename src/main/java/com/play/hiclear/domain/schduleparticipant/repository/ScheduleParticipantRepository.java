package com.play.hiclear.domain.schduleparticipant.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.schduleparticipant.entity.ScheduleParticipant;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleParticipantRepository extends JpaRepository<ScheduleParticipant, Long>, ScheduleParticipantQueryRepository {
    Optional<ScheduleParticipant> findByScheduleAndUser(Schedule schedule, User participantUser);

    default void checkIfAlreadyParticipating(Schedule schedule, User participantUser) {
        findByScheduleAndUser(schedule, participantUser)
                .ifPresent(participant -> {
                    throw new CustomException(ErrorCode.PARTICIPANT_ALREAY_EXISTED);
                });
    }

    default ScheduleParticipant findByScheduleAndUserOrThrow(Schedule schedule, User participantUser) {
        return findByScheduleAndUser(schedule, participantUser).orElseThrow(() -> new CustomException(ErrorCode.PARTICIPANT_NOT_FOUND));
    }
}
