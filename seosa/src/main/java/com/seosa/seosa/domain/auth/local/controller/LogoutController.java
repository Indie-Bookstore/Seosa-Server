package com.seosa.seosa.domain.auth.local.controller;

import com.seosa.seosa.domain.auth.local.service.LogoutService;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.global.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class LogoutController {
    private final LogoutService logoutService;

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로컬/카카오 회원로 로그아웃합니다.")
    public ResponseEntity<Map<String, String>> logout(@AuthUser User user) {
        logoutService.logout(user.getUserId());
        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }
}
