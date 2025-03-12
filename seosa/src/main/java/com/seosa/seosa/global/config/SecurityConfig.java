package com.seosa.seosa.global.config;

import com.seosa.seosa.domain.auth.oauth.service.CustomOAuth2UserService;
import com.seosa.seosa.domain.auth.oauth.CustomSuccessHandler;
import com.seosa.seosa.domain.jwt.JWTFilter;
import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.repository.RefreshTokenRepository;
import com.seosa.seosa.domain.user.repository.UserRepository;
import com.seosa.seosa.global.exception.CustomAuthenticationEntryPoint;
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
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;

    // 인증이 필요없는 URL 패턴 목록을 정의
    public static final String[] AUTH_WHITELIST = {
            "/user/checkEmail",
            "/user/checkNickname",

            "/local/login",
            "/local/signup",

            "/reissue",

            "/oauth2/**",
            "/login/oauth2/code/*",

            "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs", "/v3/api-docs/**"
    };

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CORS 설정
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                    return configuration;
                }))

                // ✅ CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // ✅ Form 로그인 비활성화
                .formLogin(auth -> auth.disable())

                // ✅ HTTP Basic 인증 비활성화
                .httpBasic(auth -> auth.disable())

                // ✅ OAuth2 로그인 설정 (카카오 로그인 경로만 허용)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                        .loginPage("/oauth2/authorization/kakao") // ✅ OAuth2 로그인 경로 지정
                )

                // ✅ 기본 LogoutFilter 제거
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/logged-out")
                        .invalidateHttpSession(true)
                        .deleteCookies("Authorization")
                        .permitAll()
                )

                // ✅ JWTFilter를 OAuth2 필터보다 먼저 실행하여 불필요한 OAuth2 리디렉트 방지
                .addFilterBefore(new JWTFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class)


                // ✅ 자동 Redirect 제거: 인증되지 않은 사용자는 401 반환
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // ✅ EntryPoint 추가
                )

                // ✅ 경로별 인가 작업
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers("/local/login").permitAll()
                        // ADMIN, EDITOR: POST API 접근 가능 / ADMIN: FAQ API 접근 가능
                        //.requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // ✅ JWT 기반 STATELESS 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
