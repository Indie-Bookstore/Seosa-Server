package com.seosa.seosa.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ✅ 현재 로그인한 사용자 객체를 컨트롤러 메서드의 매개변수로 자동 주입하는 커스텀 어노테이션
 */
@Target(ElementType.PARAMETER) // ✅ 메서드 매개변수에서만 사용 가능
@Retention(RetentionPolicy.RUNTIME) // ✅ 런타임까지 유지
public @interface Auth {
}

