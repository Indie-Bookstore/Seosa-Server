package com.seosa.seosa.domain.auth.oauth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthSignupRequest {

    @NotBlank(message = "닉네임을 입력해야 합니다.")
    private String nickname;

    private String userRoleCode; // 선택 입력
}

