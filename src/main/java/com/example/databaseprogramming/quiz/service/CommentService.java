package com.example.databaseprogramming.quiz.service;

import com.example.databaseprogramming.login.entity.User;
import com.example.databaseprogramming.login.repository.UserRepository;
import com.example.databaseprogramming.quiz.dto.CommentDto;
import com.example.databaseprogramming.quiz.entity.Comment;
import com.example.databaseprogramming.quiz.entity.Quiz;
import com.example.databaseprogramming.quiz.entity.QuizLike;
import com.example.databaseprogramming.quiz.repository.CommentRepository;
import com.example.databaseprogramming.quiz.repository.QuizLikeRepository;
import com.example.databaseprogramming.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final QuizLikeRepository quizLikeRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    // 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long quizId) {
        return commentRepository.findByQuizIdOrderByCreatedAtDesc(quizId).stream()
                .map(c -> CommentDto.builder()
                        .id(c.getId())
                        .userId(c.getUser().getId())
                        .nickname(c.getUser().getNickname())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 댓글 작성
    @Transactional
    public void addComment(Long quizId, Long userId, String content) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        commentRepository.save(Comment.builder()
                .quiz(quiz)
                .user(user)
                .content(content)
                .build());
    }

    // 좋아요 토글 (누르면 On/Off)
    @Transactional
    public Map<String, Object> toggleLike(Long quizId, Long userId) {
        boolean exists = quizLikeRepository.existsByQuizIdAndUserId(quizId, userId);

        if (exists) {
            quizLikeRepository.deleteByQuizIdAndUserId(quizId, userId); // 좋아요 취소
        } else {
            Quiz quiz = quizRepository.findById(quizId).orElseThrow();
            User user = userRepository.findById(userId).orElseThrow();
            quizLikeRepository.save(QuizLike.builder().quiz(quiz).user(user).build()); // 좋아요 추가
        }

        int newCount = quizLikeRepository.countByQuizId(quizId);
        return Map.of("liked", !exists, "count", newCount);
    }

    // 현재 좋아요 상태 조회
    @Transactional(readOnly = true)
    public Map<String, Object> getLikeStatus(Long quizId, Long userId) {
        boolean liked = quizLikeRepository.existsByQuizIdAndUserId(quizId, userId);
        int count = quizLikeRepository.countByQuizId(quizId);
        return Map.of("liked", liked, "count", count);
    }
}