package com.example.databaseprogramming.quiz.repository;

import com.example.databaseprogramming.quiz.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // 특정 유저가 퀴즈를 푼 전체 횟수 조회 (참여한 퀴즈 수)
    int countByUserId(Long userId);

    // 특정 유저가 정답을 맞춘 횟수 조회 (정답률 및 포인트 계산용)
    int countByUserIdAndIsCorrectTrue(Long userId);

}