package com.example.databaseprogramming.quiz.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    private Long userId;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
}