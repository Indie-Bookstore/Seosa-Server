package com.seosa.seosa.domain.auth.local.service;

import com.seosa.seosa.domain.auth.local.dto.request.SignupRequestDTO;
import com.seosa.seosa.domain.auth.local.dto.response.LoginResponseDTO;
import com.seosa.seosa.domain.auth.local.dto.response.SignupResponseDTO;
import com.seosa.seosa.domain.user.entity.AuthProvider;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.entity.UserRole;
import com.seosa.seosa.domain.user.repository.UserRepository;
import com.seosa.seosa.domain.user.service.UserService;
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
    private final UserService userService;

    public SignupResponseDTO signupProcess(SignupRequestDTO signupRequestDTO) {

        String email = signupRequestDTO.getEmail();

        // 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(signupRequestDTO.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        UserRole userRole = userService.determineUserRole(signupRequestDTO.getUserRoleCode());

        // 회원 데이터 저장
        User user = User.builder()
                .email(email)
                .nickname(signupRequestDTO.getNickname())
                .password(bCryptPasswordEncoder.encode(signupRequestDTO.getPassword()))
                .userRole(userRole)
                .profileImage(signupRequestDTO.getProfileImage())
                .provider(AuthProvider.LOCAL)
                .providerId(null)
                .build();

        userRepository.save(user);

        return new SignupResponseDTO("Local Signup successful", email);
    }
}
