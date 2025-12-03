package com.example.databaseprogramming.quiz.entity;

import com.example.databaseprogramming.login.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quiz_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "quiz_id"}) // 중복 좋아요 방지
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}