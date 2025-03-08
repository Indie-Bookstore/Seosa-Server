package com.seosa.seosa.domain.auth.local.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private String message;
    private String accessToken;
    private String refreshToken;
}

