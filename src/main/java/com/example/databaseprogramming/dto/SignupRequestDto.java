package com.example.databaseprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private String name;
    private String email;
    private String password;
    private String nickname;
    private LocalDate birthDate;
    private List<String> favoriteGenres;
    private Boolean agreeTerms;
    private Boolean agreeMarketing;
}
