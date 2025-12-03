package com.example.databaseprogramming.login.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private Long userId;
    private String nickname;
    private String currentPassword; // 본인 확인용
    private String newPassword;     // 변경할 비밀번호 (선택)
    private String profileImageUrl; // 변경할 이미지 URL (선택)
}