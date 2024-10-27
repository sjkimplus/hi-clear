package com.play.hiclear.domain.board.repository;

import com.play.hiclear.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByClubId(Long clubId, Pageable pageable);
}
