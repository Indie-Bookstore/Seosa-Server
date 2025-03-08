package com.seosa.seosa.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDTO {
    private String email;
    private String nickname;
    private String userRole;
    private String profileImage;
    private String created_at;
    private String updated_at;
}

