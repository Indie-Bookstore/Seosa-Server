package com.seosa.seosa;

import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Iterator;

@RestController
@ResponseBody
public class TokenTestController {

    @GetMapping("/userInfo_token")
    public String userInfoToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "인증되지 않은 사용자";
        }

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String userRole = auth.getAuthority();

        return "토큰에 저장된 값 확인 \n\n"+
                "userId: " + userId +
                "userRole: " + userRole;

    }

    @GetMapping("/userInfo_all")
    public String userInfoAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "인증되지 않은 사용자";
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Long userId = userDetails.getUserId();
        String email = userDetails.getEmail();
        String nickname = userDetails.getNickname();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        String userRoleCode = userDetails.getUserRoleCode();
        String profileImage = userDetails.getProfileImage();

        return "유저 정보 확인 \n\n" +
                "userId: " + userId + "\n" +
                "email: " + email + "\n" +
                "nickname: " + nickname + "\n" +
                "role: " + role + "\n" +
                "userRoleCode: " + userRoleCode + "\n" +
                "profileImage: " + profileImage + "\n";
    }
}
