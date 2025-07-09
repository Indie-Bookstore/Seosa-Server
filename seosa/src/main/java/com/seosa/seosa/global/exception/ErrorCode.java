package com.seosa.seosa.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // 본인인증 관련 에러
    USER_EMAIL_MISMATCH(HttpStatus.BAD_REQUEST, "입력한 이메일이 회원 정보와 일치하지 않습니다."),
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "입력한 인증번호가 이메일로 보낸 인증번호와 일치하지 않습니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증번호 입력 시간이 만료되었습니다."),

    // 회원가입 및 로그인 관련 에러
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    INVALID_ROLE_CODE(HttpStatus.BAD_REQUEST, "잘못된 코드입니다."),

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

    // 댓글 관련 에러
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND , "해당 댓글을 찾을 수 없습니다."),
    // 글 관련 에러
    INVALID_ACCESS(HttpStatus.BAD_REQUEST , "접근 권한이 없는 사용자입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND , "해당 포스트를 찾을 수 없습니다."),
    //북마크 관련 에러
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND , "해당 북마크를 찾을 수 없습니다."),
    BOOKMARK_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 북마크한 게시물입니다."),

    // FAQ 관련 에러
    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 FAQ를 찾을 수 없습니다.");





    private final HttpStatus status;
    private final String message;
}
