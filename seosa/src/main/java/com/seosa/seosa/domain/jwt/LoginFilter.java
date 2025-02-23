package com.seosa.seosa.domain.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seosa.seosa.domain.auth.local.dto.LoginDTO;
import com.seosa.seosa.domain.token.dto.TokenResponseDTO;
import com.seosa.seosa.domain.token.entity.RefreshTokenEntity;
import com.seosa.seosa.domain.token.repository.RefreshTokenRepository;
import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

// 아이디, 비밀번호 검증을 위한 로그인 커스텀 필터
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private RefreshTokenRepository refreshTokenRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;

        // 로그인 접속 주소 변경
        setFilterProcessesUrl("/local/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        LoginDTO loginDTO = new LoginDTO();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginDTO = objectMapper.readValue(messageBody, LoginDTO.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 클라이언트 요청에서 email, password 추출
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        // 유저 id와 role 가져오기
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = customUserDetails.getUserId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String userRole = auth.getAuthority();

        // access 토큰 (만료 시간 30분)
        String accessToken = jwtUtil.createJwt("accessToken", userId, userRole, 30 * 60 * 1000L);
        // refresh 토큰 (만료 시간 30일)
        String refreshToken = jwtUtil.createJwt("refreshToken", userId, userRole, 30* 24 * 60 * 60 * 1000L);

        // Refresh 토큰 저장
        addRefreshTokenEntity(userId, refreshToken, 30* 24 * 60 * 60 * 1000L);

        // JSON 응답 생성
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        try {
            // 응답 DTO 생성
            TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(accessToken, refreshToken);
            String jsonResponse = objectMapper.writeValueAsString(tokenResponseDTO);

            // JSON 응답 반환
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            log.error("Error writing JSON response", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void addRefreshTokenEntity(Long userId, String refreshToken, Long refreshTokenExpiresAt) {

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUserId(userId);
        refreshTokenEntity.setRefreshToken(refreshToken);

        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
