package com.seosa.seosa.domain.token.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String refreshToken;
    private String refreshTokenExpiresAt;

    @Version // 낙관적 락 적용
    private Integer version;
}
