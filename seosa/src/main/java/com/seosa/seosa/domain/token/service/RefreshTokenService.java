package com.seosa.seosa.domain.token.service;

import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.entity.RefreshToken;
import com.seosa.seosa.domain.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final StringRedisTemplate redisTemplate;
    private final JWTUtil jwtUtil;

    // Refresh Token 저장
    public void saveRefreshToken(Long userId, String refreshToken, Long expirationTime) {
        String key = "refreshToken:userId:" + userId;

        // ✅ 기존 토큰이 있다면 먼저 삭제
        redisTemplate.delete(key);

        // ✅ 새로운 Refresh Token 저장 (userId 기반)
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofSeconds(expirationTime));
    }

    // Refresh Token 조회
    public String getRefreshToken(Long userId) {
        String key = "refreshToken:userId:" + userId;
        return redisTemplate.opsForValue().get(key);
    }

    // Refresh Token 삭제 (로그아웃 시)
    public void deleteRefreshToken(Long userId) {
        String key = "refreshToken:userId:" + userId;
        redisTemplate.delete(key);
    }

    // ✅ 토큰으로 userId 조회 (새로운 메서드 추가)
    public Long getUserIdFromToken(String refreshToken) {
        return jwtUtil.getUserIdFromToken(refreshToken);
    }
}
