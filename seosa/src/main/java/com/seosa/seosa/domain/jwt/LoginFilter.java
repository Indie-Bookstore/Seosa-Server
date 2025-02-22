package com.seosa.seosa.domain.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seosa.seosa.domain.auth.local.dto.LoginDTO;
import com.seosa.seosa.domain.token.entity.RefreshTokenEntity;
import com.seosa.seosa.domain.token.repository.RefreshTokenRepository;
import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

// 아이디, 비밀번호 검증을 위한 로그인 커스텀 필터
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

        //Refresh 토큰 저장
        addRefreshTokenEntity(userId, refreshToken, 30* 24 * 60 * 60 * 1000L);

        // 응답 설정
        // response.addHeader("Authorization", "Bearer " + token);
        response.setHeader("accessToken", accessToken);
        response.addCookie(createCookie("refreshToken", refreshToken));
        response.setStatus(HttpStatus.OK.value());
    }

    @Transactional
    public void addRefreshTokenEntity(Long userId, String refreshToken, Long refreshTokenExpiresAt) {

        Date date = new Date(System.currentTimeMillis() + refreshTokenExpiresAt);

        // 한 사용자에 대해 하나의 refreshToken만 저장
        // 한 번에 하나의 기기만 사용한다면 문제 없으나 여러 기기를 번갈아가면서 로그인하면 매번 갱신되어 불편할 수 있음

        // 해당 사용자의 Refresh Token이 DB에 이미 존재하는지 확인
        Optional<RefreshTokenEntity> existingToken = refreshTokenRepository.findByUserId(userId);

        // 존재하는 경우 갱신, 존재하지 않는 경우 새로 저장
        if (existingToken.isPresent()) {
            RefreshTokenEntity tokenEntity = existingToken.get();
            tokenEntity.setRefreshToken(refreshToken);
            tokenEntity.setRefreshTokenExpiresAt(date.toString());
            refreshTokenRepository.saveAndFlush(tokenEntity);
        } else {
            RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
            refreshTokenEntity.setUserId(userId);
            refreshTokenEntity.setRefreshToken(refreshToken);
            refreshTokenEntity.setRefreshTokenExpiresAt(date.toString());
            refreshTokenRepository.save(refreshTokenEntity);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
