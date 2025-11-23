package com.example.databaseprogramming.quiz.repository;

import com.example.databaseprogramming.quiz.entity.Quiz;
import com.example.databaseprogramming.quiz.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findAllByOrderByCreatedAtDesc(); // 최신순 조회
    int countByCreatorId(Long creatorId);
}
