package com.seosa.seosa.domain.auth.local.service;

import com.seosa.seosa.domain.auth.local.dto.SignupDTO;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SignupService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignupService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void signupProcess(SignupDTO userSignupDTO) {

        Boolean isExist = userRepository.existsByEmail(userSignupDTO.getEmail());

        if (isExist) {
            throw new IllegalArgumentException("Email duplicate exist");
        }

        User data = User.builder()
                .email(userSignupDTO.getEmail())
                .nickname(userSignupDTO.getNickname())
                .password(bCryptPasswordEncoder.encode(userSignupDTO.getPassword()))
                .userRole(userSignupDTO.getUserRole())
                .userRoleCode(userSignupDTO.getUserRoleCode())
                .profileImage(userSignupDTO.getProfileImage())
                .build();

        userRepository.save(data);
    }
}
