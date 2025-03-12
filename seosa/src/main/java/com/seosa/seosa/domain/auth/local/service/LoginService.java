package com.seosa.seosa.domain.auth.local.service;

import com.seosa.seosa.domain.auth.local.dto.request.LoginRequestDTO;
import com.seosa.seosa.domain.auth.local.dto.response.LoginResponseDTO;
import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.service.RefreshTokenService;
import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import com.seosa.seosa.domain.user.repository.UserRepository;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public LoginResponseDTO login(LoginRequestDTO request) {

        // DB에서 이메일 존재 여부 확인
        if(!userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        try {
            log.info("🚀 로그인 시도: email={}", request.getEmail()); // ✅ 추가
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);
            log.info("✅ 인증 성공: {}", authentication.getPrincipal());

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            log.info("✅ 사용자 정보: userId={}, roles={}", userDetails.getUserId(), userDetails.getAuthorities());

            // JWT 생성
            String accessToken = jwtUtil.createJwt("access", userDetails.getUserId(), userDetails.getAuthorities().toString());
            String refreshToken = jwtUtil.createJwt("refresh", userDetails.getUserId(), userDetails.getAuthorities().toString());

            // Redis에 Refresh Token 저장
            refreshTokenService.saveRefreshToken(userDetails.getUserId(), refreshToken);
            log.info("✅ Refresh Token 저장 완료");

            // 응답 DTO 반환
            return new LoginResponseDTO("Local login successful", accessToken, refreshToken);

        } catch (Exception e) {
            log.error("❌ 인증 실패: {}", e.getMessage(), e);
            throw new RuntimeException("로그인 실패: " + e.getMessage());
        }
//        catch (BadCredentialsException e) {
//            throw new CustomException(ErrorCode.INVALID_PASSWORD);
//        }
    }
}
