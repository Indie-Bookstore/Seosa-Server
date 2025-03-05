package com.seosa.seosa.domain.auth.local.service;

import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.service.RefreshTokenService;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;

    public void logout(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.TOKEN_NOT_PROVIDED);
        }

        String accessToken = token.replace("Bearer ", "");
        Long userId;

        try {
            userId = jwtUtil.getUserId(accessToken);
        } catch (ExpiredJwtException e) {
            // ✅ 만료된 토큰에서도 userId 추출
            Claims claims = e.getClaims();
            if (claims != null) {
                userId = claims.get("userId", Long.class);
            } else {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }
        }

        // ✅ Refresh Token 삭제
        refreshTokenService.deleteRefreshToken(userId);

        // ✅ Spring Security Context 초기화
        SecurityContextHolder.clearContext();
    }
}
