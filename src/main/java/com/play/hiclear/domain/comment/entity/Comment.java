package com.play.hiclear.domain.comment.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.board.entity.Board;
import com.play.hiclear.domain.comment.dto.request.CommentUpdateRequest;
import com.play.hiclear.domain.likes.entity.Likes;
import com.play.hiclear.domain.thread.entity.Thread;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @OneToMany(mappedBy = "comment")
    private List<Likes> likes = new ArrayList<>();
    
    @OneToMany(mappedBy = "comment")
    private List<Thread> threads = new ArrayList<>();

    @NotBlank
    private String content;

    public void updateComment(CommentUpdateRequest commentUpdateRequest) {
        this.content = commentUpdateRequest.getContent();
    }
}
