package com.seosa.seosa.domain.user.service;

import com.seosa.seosa.domain.user.dto.UpdatePasswordRequestDTO;
import com.seosa.seosa.domain.user.dto.UpdateProfileRequestDTO;
import com.seosa.seosa.domain.user.dto.UserResponseDTO;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.entity.UserRole;
import com.seosa.seosa.domain.user.repository.UserRepository;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponseDTO getUserInfo(User user) {
        return new UserResponseDTO(user.getEmail(), user.getNickname(),user.getUserRole().toString(), user.getProfileImage(),
                user.getCreatedAt().toString(), user.getUpdatedAt().toString());
    }

    @Transactional
    public void updateUserProfile(User user, UpdateProfileRequestDTO request) {
        user.updateProfile(request.getNickname(), request.getProfileImage());
    }

    @Transactional
    public void updateUserPassword(User user, UpdatePasswordRequestDTO request) {
        if (!Objects.equals(request.getNewPassword1(), request.getNewPassword2())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }
        user.updatePassword(passwordEncoder.encode(request.getNewPassword1()));
    }

    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Transactional
    public void updateOAuthSignup(Long userId, String nickname, String userRoleCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserRole userRole = determineUserRole(userRoleCode);
        user.updateOAuthSignupInfo(nickname, userRole);
    }

    public UserRole determineUserRole(String userRoleCode) {
        if (userRoleCode == null || userRoleCode.isBlank()) {
            return UserRole.USER;
        }
        return switch (userRoleCode) {
            case "adminCode158" -> UserRole.ADMIN;
            case "editorCode185" -> UserRole.EDITOR;
            default -> throw new CustomException(ErrorCode.INVALID_ROLE_CODE);
        };
    }
}

