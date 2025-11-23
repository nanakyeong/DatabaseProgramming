package com.example.databaseprogramming.quiz.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class QuizDto {
    private Long id;
    private Long creatorId;
    private String creatorName; // 화면 표시용 닉네임

    private String bookTitle;
    private String bookAuthor;
    private String question;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private String correctAnswer; // 생성할 때만 씀 (클라이언트에는 안보내거나 숨김)
    private String difficulty;
    private String explanation;

    private LocalDateTime createdAt;
}