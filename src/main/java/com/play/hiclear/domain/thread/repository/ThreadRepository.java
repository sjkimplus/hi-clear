package com.play.hiclear.domain.thread.repository;

import com.play.hiclear.domain.thread.entity.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadRepository extends JpaRepository<Thread, Long> {
}
