package com.play.hiclear.domain.board.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByClubIdAndDeletedAtIsNull(Long clubId, Pageable pageable);


    default Board findByIdAndDeletedAtIsNullOrThrow(Long boardId) {
        return findById(boardId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND, Board.class.getSimpleName()));
    }
}