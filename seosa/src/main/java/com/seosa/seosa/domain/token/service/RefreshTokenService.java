package com.seosa.seosa.domain.token.service;

import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;
    private final JWTUtil jwtUtil;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // ✅ Refresh Token 저장
    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = "refreshToken:userId:" + userId;

        // 기존 토큰 삭제 후 저장
        redisTemplate.delete(key);
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofSeconds(refreshTokenExpiration));

        // 저장된 값 확인
        String savedToken = redisTemplate.opsForValue().get(key);
        System.out.println("📌 Stored Token in Redis (After Save): " + savedToken);
    }

    // ✅ Refresh Token 조회
    public String getRefreshToken(Long userId) {
        String key = "refreshToken:userId:" + userId;
        String token = redisTemplate.opsForValue().get(key);

        if (token == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "Refresh Token이 존재하지 않습니다.");
        }

        return token;
    }

    // ✅ Refresh Token 삭제 (로그아웃 시)
    public void deleteRefreshToken(Long userId) {
        String key = "refreshToken:userId:" + userId;
        redisTemplate.delete(key);
    }
}
