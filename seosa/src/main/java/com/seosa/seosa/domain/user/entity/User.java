package com.seosa.seosa.domain.user.entity;

import com.seosa.seosa.domain.faq.entity.FAQ;
import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(updatable = false)
    private String email;

    @Column(length = 30)
    private String nickname;

    @Column(length = 50)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(length = 100)
    private String roleCode;

    @Column(length = 1024)
    private String profileImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FAQ> faqs = new ArrayList<>();

    @Builder
    public User(String email , String nickname , String password , UserRole userRole , String roleCode , String profileImage){
       this.email = email;
       this.nickname = nickname;
       this.password = password;
       this.userRole = userRole;
       this.roleCode = roleCode;
       this.profileImage = profileImage;
    }


}
