package com.example.databaseprogramming.quiz.repository;

import com.example.databaseprogramming.quiz.entity.Quiz;
import com.example.databaseprogramming.quiz.entity.QuizAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Page<Quiz> findAllByOrderByCreatedAtDesc(Pageable pageable);
    int countByCreatorId(Long creatorId);
}
