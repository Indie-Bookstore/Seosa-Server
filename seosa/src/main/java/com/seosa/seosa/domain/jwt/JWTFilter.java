package com.seosa.seosa.domain.jwt;

import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.repository.UserRepository;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import com.seosa.seosa.global.config.SecurityConfig;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public JWTFilter(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();


    // SecurityConfig.AUTH_WHITELIST를 활용하여 JWT 필터 제외 경로 동기화
    private boolean isExcludedFromJwtFilter(String requestURI) {
        log.info("들어온 URL {}" , requestURI);
        return Arrays.stream(SecurityConfig.AUTH_WHITELIST)
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // JWT 검증 없이 통과
        if (isExcludedFromJwtFilter(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 Authorization 키에 담긴 토큰을 꺼냄
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = header.replace("Bearer ", "");



        // 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(accessToken);

            Long userId = jwtUtil.getUserId(accessToken);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", "ExpiredJwtException"); // ✅ 예외 정보를 request에 저장
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다.");
        } catch (MalformedJwtException e) {
            request.setAttribute("exception", "MalformedJwtException");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "잘못된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            request.setAttribute("exception", "UnsupportedJwtException");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            request.setAttribute("exception", "InvalidJwtException");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.");
        }
    }
}
