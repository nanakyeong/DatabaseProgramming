package com.example.databaseprogramming.service;

import com.example.databaseprogramming.dto.LoginRequestDto;
import com.example.databaseprogramming.dto.SignupRequestDto;
import com.example.databaseprogramming.dto.UserDto;
import com.example.databaseprogramming.entity.User;
import com.example.databaseprogramming.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    
    private final UserRepository userRepository;
    
    public UserDto login(LoginRequestDto loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다."));
        
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        
        return convertToDto(user);
    }
    
    @Transactional
    public UserDto signup(SignupRequestDto signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }
        
        if (userRepository.existsByNickname(signupRequest.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }
        
        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(signupRequest.getPassword())
                .nickname(signupRequest.getNickname())
                .birthDate(signupRequest.getBirthDate())
                .favoriteGenres(signupRequest.getFavoriteGenres())
                .agreeTerms(signupRequest.getAgreeTerms())
                .agreeMarketing(signupRequest.getAgreeMarketing())
                .build();
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
    
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .birthDate(user.getBirthDate())
                .favoriteGenres(user.getFavoriteGenres())
                .build();
    }
}
