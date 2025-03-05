package com.seosa.seosa.domain.auth.local.service;

import com.seosa.seosa.domain.auth.local.dto.LoginDTO;
import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.service.RefreshTokenService;
import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public String[] login(LoginDTO request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authToken);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // ✅ JWT 생성
        String accessToken = jwtUtil.createJwt("access", userDetails.getUserId(), userDetails.getAuthorities().toString(), 3600L); // 1시간 유효
        String refreshToken = jwtUtil.createJwt("refresh", userDetails.getUserId(), userDetails.getAuthorities().toString(), 30*24*3600L); // 30일 유효

        // ✅ Redis에 Refresh Token 저장
        refreshTokenService.saveRefreshToken(userDetails.getUserId(), refreshToken, 30*24*3600L);

        // ✅ Access Token과 Refresh Token을 배열로 반환
        return new String[]{accessToken, refreshToken};
    }
}
