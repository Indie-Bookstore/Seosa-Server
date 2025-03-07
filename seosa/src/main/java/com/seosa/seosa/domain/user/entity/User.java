package com.seosa.seosa.domain.user.entity;

import com.seosa.seosa.domain.faq.entity.FAQ;
import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, updatable = false)
    private String email;

    @Column(length = 30)
    private String nickname;

    @Column(length = 500)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(length = 100)
    private String userRoleCode;

    @Column(length = 1024)
    private String profileImage;

    @Enumerated(EnumType.STRING)  // ✅ 올바른 AuthProvider 사용
    private AuthProvider provider;

    @Column(nullable = true)
    private String providerId; // 소셜 로그인 사용자 ID

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FAQ> faqs = new ArrayList<>();

    @Builder
    public User(String email, String nickname, String password, UserRole userRole,
                String userRoleCode, String profileImage, AuthProvider provider, String providerId) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.userRole = userRole;
        this.userRoleCode = userRoleCode;
        this.profileImage = profileImage;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void updateOAuthSignupInfo(String nickname, UserRole userRole) {
        this.nickname = nickname;
        this.userRole = userRole;
    }
}
