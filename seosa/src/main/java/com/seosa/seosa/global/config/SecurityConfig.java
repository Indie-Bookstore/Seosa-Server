package com.seosa.seosa.global.config;

import com.seosa.seosa.domain.jwt.CustomLogoutFilter;
import com.seosa.seosa.domain.jwt.JWTFilter;
import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.jwt.LoginFilter;
import com.seosa.seosa.domain.token.repository.RefreshTokenRepository;
import com.seosa.seosa.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 암호화 인코더 추가
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // 인증이 필요없는 URL 패턴 목록을 정의
    private static final String[] AUTH_WHITELIST = {
            "/reissue",
            "/signup",
            "/local/login",
            "/local/logout",
            "/userInfo_all",
            "/userInfo_token",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                                CorsConfiguration configuration = new CorsConfiguration();

                                // 허용할 프론트엔드 포트
                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                                // 모든 메소드 허용
                                configuration.setAllowedMethods(Collections.singletonList("*"));

                                configuration.setAllowCredentials(true);
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L);

                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                                return configuration;
                            }
                        }))
                // csrf disable
                .csrf((auth) -> auth.disable())

                // Form 로그인 방식 disable
                .formLogin((auth) -> auth.disable())

                // http basic 인증 방식 disable
                .httpBasic((auth) -> auth.disable())

                // 경로별 인가 작업
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(AUTH_WHITELIST).permitAll() // 인증 없이 접근 가능
                        .requestMatchers("/admin").hasRole("관리자") // 인증 없이 접근 가능
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )

                // 커스텀 필터 등록 - 로그인, 로그아웃
                .addFilterBefore(new JWTFilter(jwtUtil, userRepository), LoginFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class)

                // 세선 STATELESS 설정
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}

