package com.example.databaseprogramming.quiz.dto;

import lombok.Data;

@Data
public class QuizSolveRequest {
    private Long userId;
    private Long quizId;
    private String selectedAnswer; // "A", "B"...
}