package com.seosa.seosa.domain.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.dto.TokenResponseDTO;
import com.seosa.seosa.domain.token.service.RefreshTokenService;
import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomSuccessHandler(JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = customUserDetails.getUserId();
            String role = customUserDetails.getUserRole();

            String accessToken = jwtUtil.createJwt("access", userId, role);
            String refreshToken = jwtUtil.createJwt("refresh", userId, role);

            refreshTokenService.saveRefreshToken(userId, refreshToken);

            String message = Objects.equals(role, "TEMP_USER") ?
                    "회원가입을 완료해주세요" : "OAuth2 login successful";

            // TokenResponseDTO 사용하여 응답 객체 생성
            TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(message, accessToken, refreshToken);

            // JSON 응답 반환
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(tokenResponseDTO));

        } catch (Exception e) {
            throw new CustomException(ErrorCode.OAUTH2_AUTHENTICATION_FAILED, "OAuth2 authentication success handling failed");
        }
    }

}

