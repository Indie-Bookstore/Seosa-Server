package com.seosa.seosa.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ✅ 인증되지 않은 사용자가 접근할 경우 401 에러를 반환하는 Custom Authentication Entry Point
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 🔹 예외 메시지 기본값 (로그인이 필요한 경우)
        String errorMessage = "로그인이 필요합니다.";

        // 🔹 JWT 예외 정보가 있는 경우 처리
        String exceptionType = (String) request.getAttribute("exception");
        if (exceptionType != null) {
            switch (exceptionType) {
                case "ExpiredJwtException":
                    errorMessage = "토큰이 만료되었습니다. 다시 로그인해주세요.";
                    break;
                case "MalformedJwtException":
                    errorMessage = "잘못된 JWT 토큰입니다.";
                    break;
                case "UnsupportedJwtException":
                    errorMessage = "지원되지 않는 JWT 토큰입니다.";
                    break;
                case "InvalidJwtException":
                    errorMessage = "유효하지 않은 JWT 토큰입니다.";
                    break;
            }
        }

        // 🔹 JSON 응답 생성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", 401);
        responseBody.put("code", "UNAUTHORIZED");
        responseBody.put("message", errorMessage);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

}
