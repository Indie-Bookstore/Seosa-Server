package com.seosa.seosa.domain.auth.local.controller;

import com.seosa.seosa.domain.auth.local.service.LogoutService;
import com.seosa.seosa.domain.jwt.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
public class LogoutController {
    private final LogoutService logoutService;
    private final JWTUtil jwtUtil;

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader(name = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "No token provided"));
        }

        String accessToken = token.replace("Bearer ", "");
        Long userId = null;

        try {
            userId = jwtUtil.getUserId(accessToken);
        } catch (ExpiredJwtException e) {
            // ✅ 토큰이 만료된 경우에도 userId 추출
            Claims claims = e.getClaims();
            if (claims != null) {
                userId = claims.get("userId", Long.class);
            }
        }

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
        }

        // ✅ Refresh Token 삭제 및 SecurityContext 초기화
        logoutService.logout(userId);

        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }
}
