package com.seosa.seosa.domain.token.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.dto.TokenResponseDTO;
import com.seosa.seosa.domain.token.entity.RefreshTokenEntity;
import com.seosa.seosa.domain.token.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReissueService {

    // access token 재발급시 refresh token도 재발급된다.
    // refresh token이 만료되었을 경우 (30일간 접속 안함) 재로그인이 필요하다.

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);

        // refreshToken 없음
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("refresh token null");
        }

        // 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("refresh token expired");
        }

        // 토큰의 카테고리가 "refreshToken"인지 확인
        if (!"refreshToken".equals(jwtUtil.getCategory(refreshToken))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid refresh token");
        }

        // Redis에서 refreshToken 존재 여부 확인
        Optional<RefreshTokenEntity> refreshTokenEntityOpt = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (refreshTokenEntityOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid refresh token");
        }

        RefreshTokenEntity refreshTokenEntity = refreshTokenEntityOpt.get();
        Long userId = jwtUtil.getUserId(refreshToken);
        String userRole = jwtUtil.getUserRole(refreshToken);

        // 새로운 accessToken 및 refreshToken 발급
        String newAccessToken = jwtUtil.createJwt("accessToken", userId, userRole, 30 * 60 * 1000L);
        String newRefreshToken = jwtUtil.createJwt("refreshToken", userId, userRole, 30 * 24 * 60 * 60 * 1000L);

        // 기존 refreshToken 삭제 후 새로운 refreshToken 저장
        refreshTokenRepository.delete(refreshTokenEntity);
        saveRefreshToken(userId, newRefreshToken, 30 * 24 * 60 * 60 * 1000L);

        // JSON 응답 생성
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        try {
            // 응답 DTO 생성
            TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(newAccessToken, newRefreshToken);
            String jsonResponse = objectMapper.writeValueAsString(tokenResponseDTO);

            // JSON 응답 반환
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            log.error("Error writing JSON response", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok().build();
    }

    private void saveRefreshToken(Long userId, String refreshToken, Long refreshTokenExpiresAt) {
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(refreshTokenExpiresAt / 1000);

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUserId(userId);
        refreshTokenEntity.setRefreshToken(refreshToken);
        refreshTokenEntity.setRefreshTokenExpiresAt(expiryDate);

        refreshTokenRepository.save(refreshTokenEntity);
    }

    private String extractRefreshToken(HttpServletRequest request) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> body = objectMapper.readValue(request.getInputStream(), Map.class);
            return body.get("refreshToken");
        } catch (IOException e) {
            log.error("Failed to read refreshToken from request body", e);
            return null;
        }
    }

}
