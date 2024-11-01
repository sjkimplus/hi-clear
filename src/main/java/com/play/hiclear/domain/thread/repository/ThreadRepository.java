package com.play.hiclear.domain.thread.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.thread.entity.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThreadRepository extends JpaRepository<Thread, Long> {

    Optional<Thread> findByIdAndDeletedAtIsNull(Long threadId);

    default Thread findByIdAndDeletedAtIsNullOrThrow(Long threadId){
        return findByIdAndDeletedAtIsNull(threadId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Thread.class.getSimpleName()));
    }
}
