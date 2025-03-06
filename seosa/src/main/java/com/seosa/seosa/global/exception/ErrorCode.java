package com.seosa.seosa.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다."),

    // 회원가입 및 로그인 관련 에러
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "이메일은 필수 입력 항목입니다."),
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "비밀번호는 필수 입력 항목입니다."),
    NICKNAME_REQUIRED(HttpStatus.BAD_REQUEST, "닉네임은 필수 입력 항목입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),

    // 토큰 관련 에러
    TOKEN_NOT_PROVIDED(HttpStatus.UNAUTHORIZED, "토큰이 제공되지 않았습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 JWT 형식입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT입니다."),

    // OAuth2 관련 에러 추가 (프론트와 연결 후 테스트 필요)
    OAUTH2_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "OAuth2 인증에 실패했습니다."),
    OAUTH2_PROVIDER_ERROR(HttpStatus.BAD_REQUEST, "OAuth2 제공자의 응답 처리 중 오류가 발생했습니다."),
    OAUTH2_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "소셜 로그인 계정에서 이메일 정보를 가져올 수 없습니다."),
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "지원되지 않는 OAuth2 제공자입니다."),

    // 서점 관련 에러
    BOOKSTORE_NOT_FOUND(HttpStatus.NOT_FOUND , "해당 서점을 찾을 수 없습니다."),
    // 글 관련 에러
    INVALID_ACCESS(HttpStatus.BAD_REQUEST , "접근 권한이 없는 사용자입니다.");


    private final HttpStatus status;
    private final String message;
}
