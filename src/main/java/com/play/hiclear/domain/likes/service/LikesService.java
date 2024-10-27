package com.play.hiclear.domain.likes.service;

import com.play.hiclear.domain.board.entity.Board;
import com.play.hiclear.domain.board.repository.BoardRepository;
import com.play.hiclear.domain.club.repository.ClubRepository;
import com.play.hiclear.domain.comment.entity.Comment;
import com.play.hiclear.domain.comment.repository.CommentRepository;
import com.play.hiclear.domain.likes.dto.response.LikesSearchResponse;
import com.play.hiclear.domain.likes.entity.Likes;
import com.play.hiclear.domain.likes.repository.LikesRepository;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private BoardRepository boardRepository;


    public void toggleLike(Long clubId, Long clubboardId, Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다."));

        // 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        // 게시글 확인
        Board board = boardRepository.findById(clubboardId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));


        // 기존 좋아요 상태 확인
        Likes existingLike = likesRepository.findByCommentIdAndUserId(commentId, userId)
                .orElse(null);

        if (existingLike != null) {// 이미 좋아요가 있는 경우 상태 토글
            existingLike.toggleLike();
            likesRepository.save(existingLike);
        } else {// 좋아요가 없는 경우 새 좋아요 생성
            Likes likes = new Likes(
                    user,
                    comment,
                    board.getClub()
            );

            likesRepository.save(likes);
        }
    }

    public List<LikesSearchResponse> get(Long commentId) {
        List<Likes> likes = likesRepository.findByCommentId(commentId);

        return likes.stream()
                .map(like -> new LikesSearchResponse(like.getUser().getId(), like.isStatus()))
                .collect(Collectors.toList());
    }
}
