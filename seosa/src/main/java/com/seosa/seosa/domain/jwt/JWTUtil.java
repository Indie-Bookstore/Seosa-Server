package com.seosa.seosa.domain.jwt;

import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
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

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @PostConstruct
    public void init() {
        System.out.println(" JWT Secret Key: " + secret);
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
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
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
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
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
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
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
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * ✅ JWT 생성 메서드
     */
    public String createJwt(String category, Long userId, String userRole) {
        long now = System.currentTimeMillis();

        long expiredMs;

        // 이 부분 * 1000 실제로 생성된 토큰에 어떻게 반영되는지 검증 필요
        if ("access".equals(category)) {
            expiredMs = accessTokenExpiration * 1000; // 초 → 밀리초 변환
        } else if ("refresh".equals(category)) {
            expiredMs = refreshTokenExpiration * 1000; // 초 → 밀리초 변환
        } else {
            throw new IllegalArgumentException("Invalid JWT category: " + category);
        }

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
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw e; // 만료된 토큰은 그대로 처리
        } catch (MalformedJwtException e) {
            throw new CustomException(ErrorCode.MALFORMED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    // ✅ Refresh Token에서 userId 추출
    public Long getUserIdFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("userId", Long.class);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}
