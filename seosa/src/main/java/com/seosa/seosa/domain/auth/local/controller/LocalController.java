package com.seosa.seosa.domain.auth.local.controller;

import com.seosa.seosa.domain.auth.local.dto.request.LoginRequestDTO;
import com.seosa.seosa.domain.auth.local.dto.response.LoginResponseDTO;
import com.seosa.seosa.domain.auth.local.dto.request.SignupRequestDTO;
import com.seosa.seosa.domain.auth.local.dto.response.SignupResponseDTO;
import com.seosa.seosa.domain.auth.local.service.LoginService;
import com.seosa.seosa.domain.auth.local.service.LogoutService;
import com.seosa.seosa.domain.auth.local.service.SignupService;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.global.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
public class LocalController {

    private final LoginService loginService;
    private final LogoutService logoutService;
    private final SignupService signupService;

    @PostMapping("/signup")
    @Operation(summary = "로컬 회원가입", description = "로컬 유저로 회원가입합니다.")
    public ResponseEntity<SignupResponseDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequestDTO) {
        SignupResponseDTO response = signupService.signupProcess(signupRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "로컬 로그인", description = "로컬 유저로 로그인합니다.")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = loginService.login(request);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + response.getAccessToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로컬/카카오 회원로 로그아웃합니다.")
    public ResponseEntity<Map<String, String>> logout(@AuthUser User user) {
        logoutService.logout(user.getUserId());
        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }
}
