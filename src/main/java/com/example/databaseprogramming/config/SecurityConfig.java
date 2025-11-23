package com.example.databaseprogramming.config; // 패키지명 확인!

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // [수정] 최신 문법: 람다식 사용
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // [참고] "/**"를 permitAll 하면 로그인 없이 모든 곳에 들어갈 수 있습니다 (테스트용)
                        .requestMatchers("/css/**", "/js/**","/api/auth/**", "/static/**", "/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}