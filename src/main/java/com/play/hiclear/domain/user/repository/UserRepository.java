package com.play.hiclear.domain.user.repository;

import com.play.hiclear.common.exception.CustomException;
import com.play.hiclear.common.exception.ErrorCode;
import com.play.hiclear.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    default User findByEmailAndDeletedAtIsNullOrThrow(String email){
        return findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, User.class.getSimpleName()));
    }

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    default User findByIdAndDeletedAtIsNullOrThrow(Long id){
        return findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, User.class.getSimpleName()));
    }

    Page<User> findAllByDeletedAtIsNull(Pageable pageable);
}
