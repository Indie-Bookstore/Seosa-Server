package com.seosa.seosa.domain.jwt;

import com.seosa.seosa.domain.token.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 경로 및 HTTP 메서드 확인
        if (!request.getRequestURI().equals("/local/logout") || !"POST".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // `refreshToken`을 Request Header에서 가져오기
        String refreshToken = request.getHeader("refreshToken");

        // `refreshToken`이 없으면 400 Bad Request 반환
        if (refreshToken == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Refresh token is required\"}");
            return;
        }

        // `refreshToken`이 만료되었는지 확인
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Refresh token expired\"}");
            return;
        }

        // `refreshToken`이 올바른지 확인
        if (!"refreshToken".equals(jwtUtil.getCategory(refreshToken))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid refresh token\"}");
            return;
        }

        // Redis에서 `refreshToken` 존재 여부 확인
        if (!refreshTokenRepository.existsByRefreshToken(refreshToken)) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // Redis에서 `refreshToken` 삭제
        refreshTokenRepository.deleteByRefreshToken(refreshToken);

        // 로그아웃 성공 응답
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Logged out successfully\"}");
    }
}
