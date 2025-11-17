package com.example.databaseprogramming.controller;

import com.example.databaseprogramming.dto.LoginRequestDto;
import com.example.databaseprogramming.dto.SignupRequestDto;
import com.example.databaseprogramming.dto.UserDto;
import com.example.databaseprogramming.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequest) {
        UserDto user = authService.login(loginRequest);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto signupRequest) {
        UserDto user = authService.signup(signupRequest);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(authService.existsByEmail(email));
    }
    
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(authService.existsByNickname(nickname));
    }
}
