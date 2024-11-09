package com.play.hiclear.domain.notification.repository;

import com.play.hiclear.domain.notification.entity.Noti;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Noti(알림) 객체를 저장하고 관리하는 역할
 */
public interface NotiRepository extends JpaRepository<Noti, Long> {
}
