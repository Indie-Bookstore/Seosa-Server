package com.seosa.seosa.domain.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.service.RefreshTokenService;
import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public CustomSuccessHandler(JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        // 🔹 OAuth2User 정보를 CustomUserDetails로 변환
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // 🔹 JWT 생성 (accessToken & refreshToken)
        Long userId = customUserDetails.getUserId();
        String role = customUserDetails.getAuthorities().iterator().next().getAuthority();

        String accessToken = jwtUtil.createJwt("access", userId, role, 3600L); // 1시간 유효
        String refreshToken = jwtUtil.createJwt("refresh", userId, role, 30*24*3600L); // 30일 유효

        // 🔹 Redis에 Refresh Token 저장
        refreshTokenService.saveRefreshToken(userId, refreshToken, 30*24*3600L);

        // 🔹 JSON 응답 객체 생성
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "OAuth2 login successful");
        responseBody.put("accessToken", accessToken);
        responseBody.put("refreshToken", refreshToken);

        // 🔹 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 🔹 JSON 변환 후 응답 출력
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
