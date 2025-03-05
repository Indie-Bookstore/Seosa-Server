package com.seosa.seosa.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * ✅ 인증되지 않은 사용자가 요청할 경우 401 에러를 반환하는 Custom Authentication Entry Point
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // ✅ ErrorResponse 생성
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ErrorCode.TOKEN_NOT_PROVIDED.getStatus()) // 401
                .code(ErrorCode.TOKEN_NOT_PROVIDED.name()) // "TOKEN_NOT_PROVIDED"
                .message(ErrorCode.TOKEN_NOT_PROVIDED.getMessage()) // "토큰이 제공되지 않았습니다."
                .build();

        // ✅ JSON 변환
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        // ✅ HTTP 응답 설정 (UTF-8 인코딩 추가)
        response.setContentType("application/json; charset=UTF-8"); // 🔥 인코딩 추가
        response.setCharacterEncoding(StandardCharsets.UTF_8.name()); // 🔥 UTF-8 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        response.getWriter().write(jsonResponse);
    }
}

