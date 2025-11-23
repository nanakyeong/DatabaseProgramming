package com.example.databaseprogramming.login.service;

import com.example.databaseprogramming.login.entity.User;
import com.example.databaseprogramming.login.dto.AuthResponse;
import com.example.databaseprogramming.login.dto.LoginRequest;
import com.example.databaseprogramming.login.dto.SignupRequest;
import com.example.databaseprogramming.login.dto.UserDto;
import com.example.databaseprogramming.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("이미 사용 중인 이메일입니다.")
                    .build();
        }
        
        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("이미 사용 중인 닉네임입니다.")
                    .build();
        }
        
        // 사용자 생성
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();
        
        User savedUser = userRepository.save(user);
        
        return AuthResponse.builder()
                .success(true)
                .message("회원가입이 완료되었습니다.")
                .user(UserDto.builder()
                        .id(savedUser.getId())
                        .name(savedUser.getName())
                        .email(savedUser.getEmail())
                        .nickname(savedUser.getNickname())
                        .build())
                .build();
    }
    
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);
        
        if (user == null) {
            return AuthResponse.builder()
                    .success(false)
                    .message("이메일 또는 비밀번호가 올바르지 않습니다.")
                    .build();
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("이메일 또는 비밀번호가 올바르지 않습니다.")
                    .build();
        }
        
        return AuthResponse.builder()
                .success(true)
                .message("로그인 성공")
                .user(UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .build())
                .build();
    }
}
