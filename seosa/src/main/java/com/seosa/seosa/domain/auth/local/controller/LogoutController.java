package com.seosa.seosa.domain.auth.local.controller;

import com.seosa.seosa.domain.auth.local.service.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
public class LogoutController {
    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader(name = "Authorization", required = false) String token) {
        logoutService.logout(token);
        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }
}
