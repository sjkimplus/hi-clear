package com.play.hiclear.domain.board.repository;

import com.play.hiclear.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
