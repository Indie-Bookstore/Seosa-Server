package com.seosa.seosa.domain.user.entity;


public enum UserRole {
    USER, EDITOR , ADMIN,

    // 임시 회원: 카카오 유저가 닉네임과 코드 입력를 입력하기 전 부여
    TEMP_USER
}
