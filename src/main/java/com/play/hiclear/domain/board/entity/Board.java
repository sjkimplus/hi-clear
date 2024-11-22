package com.play.hiclear.domain.board.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.club.entity.Club;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "Boards")
public class Board extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String context;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "club_id", nullable = false)
    private Club club;

    public Board(String title, String context, User user, Club club) {
        this.title = title;
        this.context = context;
        this.user = user;
        this.club = club;
    }

    public void update(String title, String context) {
        this.title = title;
        this.context = context;
    }
}