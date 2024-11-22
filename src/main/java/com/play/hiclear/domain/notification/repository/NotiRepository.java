package com.play.hiclear.domain.notification.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.notification.entity.Noti;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Noti(알림) 객체를 저장하고 관리하는 역할
 */
public interface NotiRepository extends JpaRepository<Noti, Long> {

    Optional<Noti> findById(Long id);

    default Noti findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(()-> new CustomException(ErrorCode.NO_AUTHORITY, Noti.class.getSimpleName()));
    }
}
