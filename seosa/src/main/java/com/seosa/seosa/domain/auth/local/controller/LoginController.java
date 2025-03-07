package com.seosa.seosa.domain.auth.local.controller;

import com.seosa.seosa.domain.auth.local.dto.LoginDTO;
import com.seosa.seosa.domain.auth.local.dto.LoginResponseDTO;
import com.seosa.seosa.domain.auth.local.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    @Operation(summary = "로컬 로그인", description = "로컬 유저로 로그인합니다.")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO request) {
        LoginResponseDTO response = loginService.login(request);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + response.getAccessToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }
}
