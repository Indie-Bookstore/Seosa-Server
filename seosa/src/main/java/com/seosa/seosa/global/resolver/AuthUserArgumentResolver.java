package com.seosa.seosa.global.resolver;

import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.repository.UserRepository;
import com.seosa.seosa.global.annotation.AuthUser;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import com.seosa.seosa.domain.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * ✅ 컨트롤러에서 @AuthUser를 사용하면 현재 로그인한 사용자(User 객체)를 자동으로 주입하는 Argument Resolver
 */
@Component
@RequiredArgsConstructor
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final HttpServletRequest request;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AuthUser.class) != null
                && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        // ✅ 헤더에서 Authorization 토큰 가져오기
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.TOKEN_NOT_PROVIDED);
        }

        String accessToken = token.replace("Bearer ", "");

        // ✅ 토큰이 유효한지 확인
        if (jwtUtil.isExpired(accessToken)) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        // ✅ 토큰에서 userId 추출
        Long userId = jwtUtil.getUserId(accessToken);

        // ✅ DB에서 userId로 유저 조회
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}

