package com.seosa.seosa.domain.token.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.dto.TokenResponseDTO;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);

        if (refreshToken == null) {
            throw new CustomException(ErrorCode.TOKEN_NOT_PROVIDED);
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "잘못된 Refresh Token 유형입니다.");
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        String storedToken = refreshTokenService.getRefreshToken(userId);

        if (!refreshToken.equals(storedToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "Refresh Token이 일치하지 않습니다.");
        }

        String userRole = jwtUtil.getUserRole(refreshToken);
        String newAccessToken = jwtUtil.createJwt("access", userId, userRole);
        String newRefreshToken = jwtUtil.createJwt("refresh", userId, userRole);

        refreshTokenService.saveRefreshToken(userId, newRefreshToken);

        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(newAccessToken, newRefreshToken);
        return ResponseEntity.ok(tokenResponseDTO);
    }


    private String extractRefreshToken(HttpServletRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> body = objectMapper.readValue(request.getInputStream(), Map.class);
            return body.get("refreshToken");
        } catch (IOException e) {
            log.error("Failed to read refreshToken from request body", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN, "Refresh Token을 요청에서 읽을 수 없습니다.");

        }
    }
}
