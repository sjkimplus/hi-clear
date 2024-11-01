package com.play.hiclear.domain.participant.repository;

import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.participant.entity.Participant;
import com.play.hiclear.domain.participant.enums.ParticipantRole;
import com.play.hiclear.domain.participant.enums.ParticipantStatus;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByMeetingAndUser(Meeting meeting, User user);

    int countByMeetingId(Long meetingId);

    List<Participant> findByMeetingAndStatus(Meeting meeting, ParticipantStatus status);

    List<Participant> findByUser_IdAndRoleOrderByMeeting_StartTimeAsc(Long userId, ParticipantRole role, Boolean includePassed);

    @Query("""
    SELECT p FROM Participant p 
    WHERE p.user.id = :userId 
      AND p.role = :role 
      AND (:includePassed = true OR p.meeting.endTime > CURRENT_TIMESTAMP)
    ORDER BY p.meeting.startTime ASC
    """)
    List<Participant> findByUserIdAndRoleWithConditionalEndTime(
            @Param("userId") Long userId,
            @Param("role") ParticipantRole role,
            @Param("includePassed") Boolean includePassed);

}
