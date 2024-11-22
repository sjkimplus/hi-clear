package com.play.hiclear.domain.review.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.common.enums.Ranks;
import com.play.hiclear.domain.meeting.entity.Meeting;
import com.play.hiclear.domain.review.enums.MannerRank;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Review extends TimeStamped {

    // 리뷰의 고유 Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 리뷰 작성자의 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    // 리뷰 받는사람의 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee;

    // 미팅 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MannerRank mannerRank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ranks gradeRank;

    public Review(User reviewer, User reviewee, Meeting meeting, MannerRank mannerRank, Ranks gradeRank) {
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.meeting = meeting;
        this.mannerRank = mannerRank;
        this.gradeRank = gradeRank;
    }
}
