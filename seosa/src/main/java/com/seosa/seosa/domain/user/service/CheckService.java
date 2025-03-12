package com.seosa.seosa.domain.user.service;

import com.seosa.seosa.domain.user.repository.UserRepository;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class CheckService {

    private final UserRepository userRepository;

    // 닉네임 중복확인, 유효성 검사
    public void checkNickname(@NotBlank(message = "닉네임은 필수 입력 값입니다.")
                              @Size(max = 10, message = "닉네임은 최대 10자 이내로 입력해주세요.")
                              @Pattern(regexp = "^[가-힣0-9]+$", message = "닉네임은 한글과 숫자로만 구성되어야 합니다.")
                              String nickname) {

        if(userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    // 이메일 중복확인, 유효성 검사
    public void checkEmail(@NotBlank(message = "이메일은 필수 입력 값입니다.")
                           @Email(message = "이메일 형식이 올바르지 않습니다.")
                           String email) {

        if(userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
    }
}
