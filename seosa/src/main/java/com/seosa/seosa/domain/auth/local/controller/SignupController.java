package com.seosa.seosa.domain.auth.local.controller;

import com.seosa.seosa.domain.auth.local.service.SignupService;
import com.seosa.seosa.domain.auth.local.dto.SignupDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody SignupDTO signupDTO) {
        // 필수 필드 검증
        if (signupDTO.getEmail() == null || signupDTO.getEmail().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email cannot be null"));
        }

        signupService.signupProcess(signupDTO);

        Map<String, String> response = new HashMap<>();
        response.put("message", "회원가입이 완료되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
