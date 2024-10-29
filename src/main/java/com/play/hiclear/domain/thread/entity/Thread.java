package com.play.hiclear.domain.thread.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.comment.entity.Comment;
import com.play.hiclear.domain.thread.dto.ThreadUpdateRequest;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Thread extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    private String content;

    public void updateThread(ThreadUpdateRequest threadUpdateRequest) {
        this.content = threadUpdateRequest.getContent();
    }
}
