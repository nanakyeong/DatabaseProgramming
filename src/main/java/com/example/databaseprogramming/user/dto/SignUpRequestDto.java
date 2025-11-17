package com.example.databaseprogramming.user.dto;

import com.example.databaseprogramming.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SignUpRequestDto {
    // HTML <form>의 name 속성과 일치해야 함
    private String name;
    private String email;
    private String password;
    private String passwordConfirm;
    private String nickname;
    private LocalDate birthdate;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .password(encodedPassword)
                .nickname(this.nickname)
                .birthdate(this.birthdate)
                .build();
    }
}
