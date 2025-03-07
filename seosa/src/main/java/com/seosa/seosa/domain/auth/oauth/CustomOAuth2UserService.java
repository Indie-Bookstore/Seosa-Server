package com.seosa.seosa.domain.auth.oauth;

import com.seosa.seosa.domain.auth.oauth.dto.KakaoResponse;
import com.seosa.seosa.domain.auth.oauth.dto.OAuth2Response;
import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import com.seosa.seosa.domain.user.entity.AuthProvider;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.entity.UserRole;
import com.seosa.seosa.domain.user.repository.UserRepository;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User;
        try {
            oAuth2User = super.loadUser(userRequest);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.OAUTH2_AUTHENTICATION_FAILED, e.getMessage());
        }

        // 🔹 제공자 정보 가져오기 (kakao, google 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = switch (registrationId) {
            case "kakao" -> new KakaoResponse(oAuth2User.getAttributes());
            default -> throw new CustomException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        };

        // 🔹 기존 유저 조회 (없으면 신규 유저 등록)
        User user = userRepository.findByEmail(oAuth2Response.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(oAuth2Response.getEmail())
                        .nickname(oAuth2Response.getEmail().split("@")[0]) // 이메일 아이디 기반 기본 닉네임 설정
                        .profileImage(oAuth2Response.getProfileImageUrl())
                        .provider(AuthProvider.KAKAO)
                        .providerId(oAuth2Response.getProviderId())
                        .userRole(UserRole.TEMP_USER)
                        .build()));

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }
}
