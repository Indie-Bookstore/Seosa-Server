package com.seosa.seosa.domain.auth.oauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthSignupRequest {

    @NotBlank(message = "닉네임을 입력해야 합니다.")
    @Pattern(regexp = "^[가-힣0-9]+$",
            message = "닉네임은 한글과 숫자로만 구성되어야 합니다.")
    private String nickname;

    private String userRoleCode;
}

