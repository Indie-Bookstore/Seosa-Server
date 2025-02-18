package com.seosa.seosa.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 인증이 필요없는 URL 패턴 목록을 정의
    private static final String[] AUTH_WHITELIST = {
            "/cicd",
            "/login"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_WHITELIST).permitAll() // 인증 없이 접근 가능
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )
                .formLogin(withDefaults())
                .logout(logout -> logout.permitAll());

        return http.build();
    }
}

