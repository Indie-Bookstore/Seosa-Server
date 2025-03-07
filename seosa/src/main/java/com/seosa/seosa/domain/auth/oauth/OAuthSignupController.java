package com.seosa.seosa.domain.auth.oauth;

import com.seosa.seosa.domain.auth.oauth.dto.OAuthSignupRequest;
import com.seosa.seosa.domain.auth.oauth.dto.OAuthSignupResponse;
import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.service.RefreshTokenService;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.entity.UserRole;
import com.seosa.seosa.domain.user.repository.UserRepository;
import com.seosa.seosa.domain.user.service.UserService;
import com.seosa.seosa.global.annotation.AuthUser;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
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
    public ResponseEntity<OAuthSignupResponse> completeOAuthSignup(
            @AuthUser User user, @Valid @RequestBody OAuthSignupRequest request) {

        userService.updateOAuthSignup(user.getUserId(), request.getNickname(), request.getUserRoleCode());

        String userRole = userService.determineUserRole(request.getUserRoleCode()).toString();
        String accessToken = jwtUtil.createJwt("access", user.getUserId(), userRole);
        String refreshToken = jwtUtil.createJwt("refresh", user.getUserId(), userRole);

        refreshTokenService.saveRefreshToken(user.getUserId(), refreshToken);

        return ResponseEntity.ok(new OAuthSignupResponse(
                "OAuth2 signup successful",
                accessToken,
                refreshToken
        ));
    }
}
