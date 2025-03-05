package com.seosa.seosa.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ✅ 현재 요청을 보낸 사용자의 User 객체를 컨트롤러 매개변수에 자동 주입하는 커스텀 어노테이션
 */
@Target(ElementType.PARAMETER) // ✅ 메서드의 매개변수에서만 사용 가능
@Retention(RetentionPolicy.RUNTIME) // ✅ 런타임에도 유지
public @interface AuthUser {
}

