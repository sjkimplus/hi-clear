package com.play.hiclear.domain.meeting.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Optional<Meeting> findByIdAndDeletedAtIsNull(Long id);

    default Meeting findByIdAndDeletedAtIsNullOrThrow(Long id){
        return findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Meeting.class.getSimpleName()));
    }
}
