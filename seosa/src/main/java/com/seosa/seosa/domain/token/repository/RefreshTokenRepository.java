package com.seosa.seosa.domain.token.repository;

import com.seosa.seosa.domain.token.entity.RefreshTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> {

    boolean existsByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    Optional<RefreshTokenEntity> findByUserId(Long userId);

    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);
}
