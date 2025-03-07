package com.seosa.seosa.domain.auth.local.service;

import com.seosa.seosa.domain.auth.local.dto.LoginDTO;
import com.seosa.seosa.domain.auth.local.dto.LoginResponseDTO;
import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.service.RefreshTokenService;
import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import com.seosa.seosa.domain.user.repository.UserRepository;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public LoginResponseDTO login(LoginDTO request) {

        // 이메일 및 비밀번호 검증
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new CustomException(ErrorCode.EMAIL_REQUIRED);
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new CustomException(ErrorCode.PASSWORD_REQUIRED);
        }

        // DB에서 이메일 존재 여부 확인
        if(!userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // JWT 생성
            String accessToken = jwtUtil.createJwt("access", userDetails.getUserId(), userDetails.getAuthorities().toString());
            String refreshToken = jwtUtil.createJwt("refresh", userDetails.getUserId(), userDetails.getAuthorities().toString());

            // Redis에 Refresh Token 저장
            refreshTokenService.saveRefreshToken(userDetails.getUserId(), refreshToken);

            // 응답 DTO 반환
            return new LoginResponseDTO("Local login successful", accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
