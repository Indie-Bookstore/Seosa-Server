package com.seosa.seosa.domain.auth.local.controller;

import com.seosa.seosa.domain.auth.local.dto.LoginDTO;
import com.seosa.seosa.domain.auth.local.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO request) {
        String[] tokens = loginService.login(request);
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        return ResponseEntity.ok()
                .headers(headers)
                .body(Map.of(
                        "message", "Local login successful",
                        "accessToken", accessToken,
                        "refreshToken", refreshToken
                ));
    }
}
