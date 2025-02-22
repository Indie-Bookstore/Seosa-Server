package com.seosa.seosa.domain.user.repository;

import com.seosa.seosa.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {

    Boolean existsByNickname(String nickname);

    // email을 받아 db 테이블에서 회원을 조회
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}
