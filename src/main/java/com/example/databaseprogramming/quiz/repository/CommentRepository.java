package com.example.databaseprogramming.quiz.repository;

import com.example.databaseprogramming.quiz.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByQuizIdOrderByCreatedAtDesc(Long quizId);
}