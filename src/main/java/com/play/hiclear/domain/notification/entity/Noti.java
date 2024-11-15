package com.play.hiclear.domain.notification.entity;

import com.play.hiclear.common.entity.TimeStamped;
import com.play.hiclear.domain.notification.enums.NotiType;
import com.play.hiclear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Noti extends TimeStamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String url;

    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    private NotiType notiType;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    public void read(){
        isRead = true;
    }
}