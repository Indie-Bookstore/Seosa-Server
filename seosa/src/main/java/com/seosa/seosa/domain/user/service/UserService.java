package com.seosa.seosa.domain.user.service;

import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.entity.UserRole;
import com.seosa.seosa.domain.user.repository.UserRepository;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional // ✅ 변경 사항이 자동으로 DB에 반영됨
    public void updateOAuthSignup(Long userId, String nickname, String userRoleCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserRole userRole = determineUserRole(userRoleCode);
        user.updateOAuthSignupInfo(nickname, userRole);

        // ✅ JPA의 변경 감지(Dirty Checking) 기능으로 자동으로 업데이트됨
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

