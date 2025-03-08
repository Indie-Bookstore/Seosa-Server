package com.seosa.seosa.domain.auth.local.dto;

import com.seosa.seosa.domain.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SignupDTO {
    private String email;
    private String nickname;
    private String password;
    private UserRole userRole;
    private String userRoleCode;
    private String profileImage;

    @Builder
    public SignupDTO(String email, String nickname, String password, UserRole userRole, String userRoleCode, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.userRole = userRole;
        this.userRoleCode = userRoleCode;
        this.profileImage = profileImage;
    }
}
