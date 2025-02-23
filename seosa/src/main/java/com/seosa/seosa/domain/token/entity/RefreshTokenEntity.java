package com.seosa.seosa.domain.token.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 30*86400) // TTL 30일 (86400초)
public class RefreshTokenEntity {

    @Id
    private String refreshToken;
    @Indexed
    private Long userId;
    private LocalDateTime refreshTokenExpiresAt;

    public boolean isExpired() {
        return refreshTokenExpiresAt.isBefore(LocalDateTime.now());
    }

    public RefreshTokenEntity(String refreshToken, Long userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}

