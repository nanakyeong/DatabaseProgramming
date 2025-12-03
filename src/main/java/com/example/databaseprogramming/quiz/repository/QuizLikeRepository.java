package com.example.databaseprogramming.quiz.repository;

import com.example.databaseprogramming.quiz.entity.QuizLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizLikeRepository extends JpaRepository<QuizLike, Long> {
    boolean existsByQuizIdAndUserId(Long quizId, Long userId);
    void deleteByQuizIdAndUserId(Long quizId, Long userId);
    int countByQuizId(Long quizId);

    // [랭킹용] 특정 유저가 만든 퀴즈들이 받은 총 좋아요 수
    @Query("SELECT COUNT(ql) FROM QuizLike ql WHERE ql.quiz.creator.id = :creatorId")
    int countTotalLikesReceivedByCreator(@Param("creatorId") Long creatorId);
}