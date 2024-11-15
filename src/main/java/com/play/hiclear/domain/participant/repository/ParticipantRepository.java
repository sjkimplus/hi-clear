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

    List<Participant> findByMeetingAndStatus(Meeting meeting, ParticipantStatus status);

    @Query("SELECT p FROM Participant p WHERE p.meeting.finished = true AND p.meeting IN " +
            "(SELECT subP.meeting FROM Participant subP WHERE subP.user.id = :userId)")
    List<Participant> findFinishedMeetingsUserJoined(@Param("userId") Long userId);


    @Query("""
    SELECT COUNT(p) FROM Participant p 
    WHERE p.meeting = :meeting 
      AND p.status = :status
    """)
    int countByMeetingAndStatus(
            @Param("meeting") Meeting meeting,
            @Param("status") ParticipantStatus status);


    @Query("""

    SELECT p FROM Participant p
    WHERE p.user.id = :userId
      AND p.role = :role
      AND p.meeting.finished = false
      AND p.meeting.deletedAt IS NULL
    ORDER BY p.meeting.startTime ASC
    """)
    List<Participant> findByUserIdAndRoleExcludingFinished(
            @Param("userId") Long userId,
            @Param("role") ParticipantRole role);
    }
