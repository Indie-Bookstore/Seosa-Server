package com.seosa.seosa.global.resolver;

import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import com.seosa.seosa.global.annotation.Auth;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * ✅ 컨트롤러에서 @Auth를 사용하면 현재 로그인한 사용자 객체를 자동으로 매개변수에 주입하는 Argument Resolver
 */
@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(Auth.class) != null
                && parameter.getParameterType().equals(CustomUserDetails.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new CustomException(ErrorCode.TOKEN_NOT_PROVIDED); // 401 Unauthorized
        }

        return authentication.getPrincipal(); // ✅ 현재 로그인한 사용자 정보 반환
    }
}

