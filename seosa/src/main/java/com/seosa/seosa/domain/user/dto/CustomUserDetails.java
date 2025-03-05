package com.seosa.seosa.domain.user.dto;

import com.seosa.seosa.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes; // ✅ OAuth2 속성 저장을 위한 필드 추가

    // 로컬 로그인용 생성자
    public CustomUserDetails(User user) {
        this.user = user;
    }

    // OAuth2 로그인용 생성자 (OAuth2User에서 받은 attributes 저장)
    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().toString()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserId().toString();
    }

    public Long getUserId() {
        return user.getUserId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public String getUserRole() {
        return user.getUserRole().toString();
    }

    public String getUserRoleCode() {
        return user.getUserRoleCode();
    }

    public String getProfileImage() {
        return user.getProfileImage();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // ✅ OAuth2User 인터페이스 구현 (소셜 로그인 정보 저장)
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getNickname();
    }
}
