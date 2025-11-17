package com.example.databaseprogramming.user.controller;

import com.example.databaseprogramming.user.dto.SignUpRequestDto;
import com.example.databaseprogramming.user.dto.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // [중요] HTML <form>의 action과 일치
    @PostMapping("/signup")
    public String signup(SignUpRequestDto requestDto) {
        try {
            userService.registerUser(requestDto);
        } catch (IllegalArgumentException e) {
            // TODO: 중복 또는 비밀번호 불일치 시 에러 처리
            // 지금은 간단히 회원가입 페이지로 리다이렉트 (에러 메시지 포함)
            return "redirect:/?signup=error&message=" + e.getMessage();
        }

        // 회원가입 성공
        return "redirect:/?signup=success"; // 성공 시 로그인 모달을 띄우도록 리다이렉트
    }
}