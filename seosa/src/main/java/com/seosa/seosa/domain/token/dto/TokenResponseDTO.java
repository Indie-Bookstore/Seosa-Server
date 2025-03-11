package com.seosa.seosa.domain.token.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDTO {
    private String message;
    private String accessToken;
    private String refreshToken;
}

