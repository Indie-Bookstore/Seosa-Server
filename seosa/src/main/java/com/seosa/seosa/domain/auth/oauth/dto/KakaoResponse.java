package com.seosa.seosa.domain.auth.oauth.dto;

import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;

import java.util.Map;
import java.util.Optional;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        try {
            return String.valueOf(attributes.get("id"));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.OAUTH2_PROVIDER_ERROR, "Failed to extract provider ID");
        }
    }

    @Override
    public String getEmail() {
        try {
            return Optional.ofNullable((Map<String, Object>) attributes.get("kakao_account"))
                    .map(acc -> (String) acc.get("email"))
                    .orElseThrow(() -> new CustomException(ErrorCode.OAUTH2_EMAIL_NOT_FOUND));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.OAUTH2_PROVIDER_ERROR, "Failed to extract email");
        }
    }

    @Override
    public String getProfileImageUrl() {
        try {
            return Optional.ofNullable((Map<String, Object>) attributes.get("kakao_account"))
                    .map(acc -> (Map<String, Object>) acc.get("profile"))
                    .map(profile -> (String) profile.get("profile_image_url"))
                    .orElse(null);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.OAUTH2_PROVIDER_ERROR, "Failed to extract profile image URL");
        }
    }
}
