package com.play.hiclear.domain.meeting.repository;

import com.play.hiclear.domain.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MeetingRepository extends JpaRepository<Meeting, Long> {

}
