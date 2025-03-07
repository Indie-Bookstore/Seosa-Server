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

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // ✅ `MethodArgumentNotValidException` 발생 여부 확인 후 400 응답 처리
        if (request.getAttribute("org.springframework.validation.BindingResult") != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write("{ \"status\": \"BAD_REQUEST\", \"message\": \"요청 데이터가 유효하지 않습니다.\" }");
            return;
        }

        // ✅ 기본적으로 토큰 미제공 시 401 반환
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write("{ \"status\": \"UNAUTHORIZED\", \"code\": \"INVALID_REQUEST\", \"message\": \"요청 형식에 오류가 있습니다.\" }");
    }
}

