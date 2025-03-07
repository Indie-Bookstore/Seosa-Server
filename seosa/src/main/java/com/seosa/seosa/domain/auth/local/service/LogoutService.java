package com.seosa.seosa.domain.auth.local.service;

import com.seosa.seosa.domain.token.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void logout(Long userId) {
        // Refresh Token 삭제
        refreshTokenService.deleteRefreshToken(userId);

        // Spring Security Context 초기화
        SecurityContextHolder.clearContext();

    }
}
