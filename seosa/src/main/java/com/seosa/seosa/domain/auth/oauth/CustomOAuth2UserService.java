package com.seosa.seosa.domain.auth.oauth;

import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import com.seosa.seosa.domain.user.entity.AuthProvider;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.entity.UserRole;
import com.seosa.seosa.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 🔹 제공자 정보 가져오기 (kakao, google 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("지원되지 않는 소셜 로그인 제공자입니다: " + registrationId);
        }

        // 🔹 기존 유저 조회
        Optional<User> existingUser = userRepository.findByEmail(oAuth2Response.getEmail());
        User user;

        if (existingUser.isPresent()) {
            // 유저 이미 존재 -> 로그인
            user = existingUser.get();
        } else {
            // 신규 유저 -> 회원가입 후 로그인
            user = userRepository.save(User.builder()
                    .email(oAuth2Response.getEmail())
                    .nickname("카카오 사용자") // 기본 닉네임 설정
                    .profileImage(oAuth2Response.getProfileImageUrl())
                    .provider(AuthProvider.KAKAO)
                    .providerId(oAuth2Response.getProviderId())
                    .userRole(UserRole.USER)
                    .build());
        }

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }
}
