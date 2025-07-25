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
import org.springframework.web.util.UriComponentsBuilder;

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

            String uri;

            // 임시 유저(최초 로그인 상태)
            // 추가 정보기입 페이지로 리다이렉트 (닉네임과 인증코드 입력 페이지)
            if (Objects.equals(role, "TEMP_USER")) {
                uri = "seosa://onboarding";
                // uri값 예시: myapp://oauthdomain
                // 완료시 이런식으로 리다이렉트됩니다: myapp://oauthdomain?accessToken=eyJ&refreshToken=ey
            }

            // 일반 유저
            // 메인 페이지로 리다이렉트
            else {
                uri = "seosa://home";
            }

            System.out.println(uri);

            String targetUrl = UriComponentsBuilder.fromUriString(uri)
                    .queryParam("accessToken", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.OAUTH2_AUTHENTICATION_FAILED, "OAuth2 authentication success handling failed");
        }
    }

}

