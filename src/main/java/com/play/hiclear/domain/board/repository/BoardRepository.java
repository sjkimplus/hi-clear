package com.play.hiclear.domain.board.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByClubId(Long clubId, Pageable pageable);

    Optional<Board> findById(Long boardId);

    default Board findBoardIdOrThrow(Long boardId) {
        return findById(boardId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND, Board.class.getSimpleName()));
    }
}