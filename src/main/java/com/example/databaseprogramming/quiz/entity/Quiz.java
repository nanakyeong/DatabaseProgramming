package com.example.databaseprogramming.quiz.entity;

import com.example.databaseprogramming.login.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "quizzes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator; // 출제자

    private String bookTitle;   // 책 제목
    private String bookAuthor;  // 책 저자

    @Column(nullable = false)
    private String question;    // 질문

    // 4지 선다
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    @Column(nullable = false)
    private String correctAnswer; // 정답 (A, B, C, D)

    private String difficulty;    // 난이도 (쉬움, 보통, 어려움)

    @Column(columnDefinition = "TEXT")
    private String explanation;   // 해설

    @CreationTimestamp
    private LocalDateTime createdAt;
}