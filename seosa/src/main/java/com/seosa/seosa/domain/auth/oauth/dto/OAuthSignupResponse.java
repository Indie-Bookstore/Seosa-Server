package com.seosa.seosa.domain.auth.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthSignupResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
}

