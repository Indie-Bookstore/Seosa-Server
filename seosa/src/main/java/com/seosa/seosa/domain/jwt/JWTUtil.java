package com.seosa.seosa.domain.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    public void init() {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * ✅ 토큰에서 userId 추출 (만료된 토큰에서도 가능)
     */
    public Long getUserId(String token) {
        try {
            return parseClaims(token).get("userId", Long.class);
        } catch (ExpiredJwtException e) {
            return e.getClaims().get("userId", Long.class); // ✅ 만료된 토큰에서도 userId 반환
        }
    }

    /**
     * ✅ 토큰에서 userRole 추출
     */
    public String getUserRole(String token) {
        try {
            return parseClaims(token).get("userRole", String.class);
        } catch (ExpiredJwtException e) {
            return e.getClaims().get("userRole", String.class);
        }
    }

    /**
     * ✅ 토큰에서 category 추출
     */
    public String getCategory(String token) {
        try {
            return parseClaims(token).get("category", String.class);
        } catch (ExpiredJwtException e) {
            return e.getClaims().get("category", String.class);
        }
    }

    /**
     * ✅ 토큰 만료 여부 확인
     */
    public Boolean isExpired(String token) {
        try {
            Date expiration = parseClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // ✅ 만료된 경우 true 반환
        }
    }

    /**
     * ✅ JWT 생성 메서드
     */
    public String createJwt(String category, Long userId, String userRole, Long expiredMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claim("category", category)
                .claim("userId", userId)
                .claim("userRole", userRole)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * ✅ JWT Claims 파싱 (예외 발생 시 처리)
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ✅ Refresh Token에서 userId 추출
    public Long getUserIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", Long.class);
    }
}
