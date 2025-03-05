package com.seosa.seosa;

import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@ResponseBody
public class TokenTestController {

    @GetMapping("/userInfo_Map")
    public Map<String, Object> userInfoAll1() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userDetails.getUserId());
        response.put("email", userDetails.getEmail());
        response.put("nickname", userDetails.getNickname());
        response.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
        response.put("userRoleCode", userDetails.getUserRoleCode());
        response.put("profileImage", userDetails.getProfileImage());

        return response;
    }


    // DB에 저장된 정보에 접근
    // DTO 객체 반환
    @GetMapping("/userInfo_DTO")
    public UserInfoResponse userInfoAll2() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return new UserInfoResponse(
                userDetails.getUserId(),
                userDetails.getEmail(),
                userDetails.getNickname(),
                userDetails.getAuthorities().iterator().next().getAuthority(),
                userDetails.getUserRoleCode(),
                userDetails.getProfileImage()
        );
    }

    // 응답 DTO
    record UserInfoResponse(Long userId, String email, String nickname, String role, String userRoleCode, String profileImage) {}

}
