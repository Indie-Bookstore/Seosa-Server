package com.seosa.seosa.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequestDTO {

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,}$",
            message = "비밀번호는 최소 8자 이상이며, 최소 1개 이상의 영문자와 숫자를 포함해야 합니다.")
    private String newPassword1;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,}$",
            message = "비밀번호는 최소 8자 이상이며, 최소 1개 이상의 영문자와 숫자를 포함해야 합니다.")
    private String newPassword2;
}

