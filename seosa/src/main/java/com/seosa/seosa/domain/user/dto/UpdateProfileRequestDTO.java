package com.seosa.seosa.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequestDTO {
    @Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다.")
    @Pattern(regexp = "^[가-힣0-9]+$", message = "닉네임은 한글과 숫자로만 구성해야 합니다.")
    private String nickname;

    private String profileImage;  // 프로필 이미지 URL
}
