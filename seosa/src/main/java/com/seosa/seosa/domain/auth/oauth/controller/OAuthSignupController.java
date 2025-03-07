package com.seosa.seosa.domain.auth.oauth.controller;

import com.seosa.seosa.domain.auth.oauth.dto.OAuthSignupRequest;
import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.dto.TokenResponseDTO;
import com.seosa.seosa.domain.token.service.RefreshTokenService;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.service.UserService;
import com.seosa.seosa.global.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuthSignupController {

    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PatchMapping("/signup")
    @Operation(summary = "카카오 회원가입", description = "카카오 유저가 닉네임과 코드를 입력해 회원가입합니다.")
    public ResponseEntity<TokenResponseDTO> completeOAuthSignup(
            @AuthUser User user, @Valid @RequestBody OAuthSignupRequest request) {

        userService.updateOAuthSignup(user.getUserId(), request.getNickname(), request.getUserRoleCode());

        String userRole = userService.determineUserRole(request.getUserRoleCode()).toString();
        String accessToken = jwtUtil.createJwt("access", user.getUserId(), userRole);
        String refreshToken = jwtUtil.createJwt("refresh", user.getUserId(), userRole);

        refreshTokenService.saveRefreshToken(user.getUserId(), refreshToken);

        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO("OAuth2 signup successful", accessToken, refreshToken);
        return ResponseEntity.ok(tokenResponseDTO);
    }
}
