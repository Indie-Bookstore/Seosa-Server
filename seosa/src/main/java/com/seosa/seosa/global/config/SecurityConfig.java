package com.seosa.seosa.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 인증이 필요없는 URL 패턴 목록을 정의
    private static final String[] AUTH_WHITELIST = {
            "/cicd",
            "/login",
            "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs", "/v3/api-docs/**"
    };

    // cors 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000")); // 프론트 localhost 허용 , 포트 번호 다르다면 추가하거나 *로 하면 됩니다.
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // http의 특정 보안 구성을 비활성화
        http
                .csrf(csrf -> csrf.disable()) // CSRF 토큰 비활성화
                .formLogin(formLogin -> formLogin.disable()) // 폼 로그인 비활성화 (JWT 사용)
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP 기본 인증 비활성화
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> {
                    // 세션 사용하지 않음 (Stateless)
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                });
        // 아직 회원가입 / 로그인 관련 에러 핸들링이 구현되어있지 않습니다. 추가하셔야해요.

        http.authorizeHttpRequests(auth -> {
            // AUTH_WHITELIST에 정의된 URL은 모든 사용자에게 접근 허용
            auth.requestMatchers(AUTH_WHITELIST).permitAll();
            // 그 외의 요청은 인증 필요
            auth.anyRequest().authenticated();
        });

        return http.build();
    }
}

