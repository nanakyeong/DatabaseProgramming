package com.example.databaseprogramming.user.security;

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
        // 비밀번호 암호화를 위한 BCrypt 인코더
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 비활성화 (Thymeleaf 사용 시 활성화 권장되나, 지금은 편의상 비활성화)
                .csrf(csrf -> csrf.disable())

                // 2. 인증/인가 설정
                .authorizeHttpRequests(auth -> auth
                        // '/static/**' 리소스 (css, js 등)는 누구나 접근 허용
                        .requestMatchers("/static/**", "/css/**", "/js/**").permitAll()
                        // 홈페이지 ('/'), 회원가입 페이지 ('/signup')는 누구나 접근 허용
                        .requestMatchers("/", "/index.html", "/signup").permitAll()
                        // 그 외 모든 요청은 인증된 사용자만 접근 가능
                        .anyRequest().authenticated()
                )

                // 3. 폼 로그인 설정
                .formLogin(form -> form
                        // 로그인 페이지는 홈페이지(/)로 설정 (모달이 있는 곳)
                        .loginPage("/")
                        // [중요] HTML <form>의 action과 일치해야 함
                        .loginProcessingUrl("/login")
                        // [중요] HTML <input>의 name과 일치해야 함
                        .usernameParameter("email")
                        .passwordParameter("password")
                        // 로그인 성공 시 이동할 기본 페이지
                        .defaultSuccessUrl("/index.html?login=success", true)
                        // 로그인 실패 시 (모달을 다시 띄우기 위해 파라미터 추가)
                        .failureUrl("/index.html?login=error")
                        .permitAll() // 로그인 관련 페이지는 누구나 접근 가능해야 함
                )

                // 4. 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 처리 URL
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 리다이렉트
                );

        return http.build();
    }
}