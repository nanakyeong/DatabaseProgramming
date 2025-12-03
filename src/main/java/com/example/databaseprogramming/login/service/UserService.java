package com.example.databaseprogramming.login.service;

import com.example.databaseprogramming.login.dto.UserUpdateDto;
import com.example.databaseprogramming.login.entity.User;
import com.example.databaseprogramming.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 정보 수정
    @Transactional
    public void updateProfile(UserUpdateDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. 현재 비밀번호 확인 (보안)
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 2. 닉네임 변경 (중복 체크 필요 시 추가 가능)
        if (dto.getNickname() != null && !dto.getNickname().isEmpty()) {
            user.setNickname(dto.getNickname());
        }

        // 3. 비밀번호 변경 (입력된 경우에만)
        if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        // 4. 프로필 이미지 변경
        if (dto.getProfileImageUrl() != null) {
            user.setProfileImageUrl(dto.getProfileImageUrl());
        }
    }

    // 회원 탈퇴
    @Transactional
    public void withdraw(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(user);
    }
}