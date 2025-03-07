package com.seosa.seosa.domain.auth.local.service;

import com.seosa.seosa.domain.auth.local.dto.SignupDTO;
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

    public void signupProcess(SignupDTO signupDTO) {

        // 이메일 중복 체크
        if (userRepository.existsByEmail(signupDTO.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        UserRole userRole = userService.determineUserRole(signupDTO.getUserRoleCode());

        // 회원 데이터 저장
        User user = User.builder()
                .email(signupDTO.getEmail())
                .nickname(signupDTO.getNickname())
                .password(bCryptPasswordEncoder.encode(signupDTO.getPassword()))
                .userRole(userRole)
                .profileImage(signupDTO.getProfileImage())
                .provider(AuthProvider.LOCAL)
                .providerId(null)
                .build();

        userRepository.save(user);
    }
}
