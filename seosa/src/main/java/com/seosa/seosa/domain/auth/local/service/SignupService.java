package com.seosa.seosa.domain.auth.local.service;

import com.seosa.seosa.domain.auth.local.dto.SignupDTO;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.repository.UserRepository;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void signupProcess(SignupDTO signupDTO) {
        // ✅ 필수 필드 검증 (이메일, 비밀번호, 닉네임)
        if (signupDTO.getEmail() == null || signupDTO.getEmail().isEmpty()) {
            throw new CustomException(ErrorCode.EMAIL_REQUIRED);
        }
        if (signupDTO.getPassword() == null || signupDTO.getPassword().isEmpty()) {
            throw new CustomException(ErrorCode.PASSWORD_REQUIRED);
        }
        if (signupDTO.getNickname() == null || signupDTO.getNickname().isEmpty()) {
            throw new CustomException(ErrorCode.NICKNAME_REQUIRED);
        }

        // ✅ 이메일 중복 체크
        if (userRepository.existsByEmail(signupDTO.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // ✅ 회원 데이터 저장
        User user = User.builder()
                .email(signupDTO.getEmail())
                .nickname(signupDTO.getNickname())
                .password(bCryptPasswordEncoder.encode(signupDTO.getPassword()))
                .userRole(signupDTO.getUserRole())
                .userRoleCode(signupDTO.getUserRoleCode())
                .profileImage(signupDTO.getProfileImage())
                .build();

        userRepository.save(user);
    }
}
