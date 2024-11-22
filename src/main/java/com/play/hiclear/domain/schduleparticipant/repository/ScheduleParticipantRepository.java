package com.play.hiclear.domain.schduleparticipant.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.schduleparticipant.entity.ScheduleParticipant;
import com.play.hiclear.domain.schedule.entity.Schedule;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScheduleParticipantRepository extends JpaRepository<ScheduleParticipant, Long>, ScheduleParticipantQueryRepository {
    Optional<ScheduleParticipant> findByScheduleAndUser(Schedule schedule, User participantUser);

    // 일정에 속한 모든 참가자 삭제
    @Modifying
    @Query("DELETE FROM ScheduleParticipant sp WHERE sp.schedule = :schedule")
    void deleteParticipantsBySchedule(@Param("schedule") Schedule schedule);

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
