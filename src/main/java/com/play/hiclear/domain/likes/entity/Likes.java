package com.play.hiclear.domain.likes.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.user.entity.User;
import com.play.hiclear.domain.comment.entity.Comment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Likes extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    public Likes(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
        this.status = true;
    }

    public void toggleLike() {
        this.status = !this.status; // 좋아요 상태 변경
    }
}
