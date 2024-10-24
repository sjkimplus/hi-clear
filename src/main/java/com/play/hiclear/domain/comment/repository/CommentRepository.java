package com.play.hiclear.domain.comment.repository;

import com.play.hiclear.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
