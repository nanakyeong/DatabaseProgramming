package com.example.databaseprogramming.login.controller;

import com.example.databaseprogramming.login.dto.UserUpdateDto;
import com.example.databaseprogramming.login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    // 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserUpdateDto dto) {
        try {
            userService.updateProfile(dto);
            return ResponseEntity.ok(Map.of("success", true, "message", "회원 정보가 수정되었습니다. 다시 로그인해주세요."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 회원 탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestParam Long userId, @RequestParam String password) {
        try {
            userService.withdraw(userId, password);
            return ResponseEntity.ok(Map.of("success", true, "message", "회원 탈퇴가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}