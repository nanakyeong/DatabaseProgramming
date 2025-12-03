package com.example.databaseprogramming.quiz.service;

import com.example.databaseprogramming.login.entity.User;
import com.example.databaseprogramming.login.repository.UserRepository;
import com.example.databaseprogramming.quiz.dto.QuizDto;
import com.example.databaseprogramming.quiz.dto.QuizSolveRequest;
import com.example.databaseprogramming.quiz.entity.Quiz;
import com.example.databaseprogramming.quiz.entity.QuizAttempt;
import com.example.databaseprogramming.quiz.repository.QuizAttemptRepository;
import com.example.databaseprogramming.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // 추가
import org.springframework.data.domain.Pageable; // 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;

    // 퀴즈 생성 (기존 유지)
    @Transactional
    public void createQuiz(QuizDto dto) {
        // ... (기존 코드와 동일)
        User user = userRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Quiz quiz = Quiz.builder()
                .creator(user)
                .bookTitle(dto.getBookTitle())
                .bookAuthor(dto.getBookAuthor())
                .question(dto.getQuestion())
                .optionA(dto.getOptionA())
                .optionB(dto.getOptionB())
                .optionC(dto.getOptionC())
                .optionD(dto.getOptionD())
                .correctAnswer(dto.getCorrectAnswer())
                .difficulty(dto.getDifficulty())
                .explanation(dto.getExplanation())
                .build();

        quizRepository.save(quiz);
    }

    // [수정] 퀴즈 목록 조회 (페이징 적용)
    @Transactional(readOnly = true)
    public Page<QuizDto> getAllQuizzes(Pageable pageable) {
        // Repository가 Page<Quiz>를 반환하므로 map을 통해 DTO로 변환
        return quizRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(q -> QuizDto.builder()
                        .id(q.getId())
                        .creatorName(q.getCreator().getNickname())
                        .bookTitle(q.getBookTitle())
                        .question(q.getQuestion())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .difficulty(q.getDifficulty())
                        .build());
    }

    // solveQuiz, getUserStats 등 나머지 메서드는 기존 유지...
    @Transactional
    public Map<String, Object> solveQuiz(QuizSolveRequest request) {
        // ... (기존 코드 생략 - 변경 없음)
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isCorrect = quiz.getCorrectAnswer().equalsIgnoreCase(request.getSelectedAnswer());

        // 기록 저장
        QuizAttempt attempt = QuizAttempt.builder()
                .user(user)
                .quiz(quiz)
                .isCorrect(isCorrect)
                .build();
        quizAttemptRepository.save(attempt);

        return Map.of(
                "correct", isCorrect,
                "message", isCorrect ? "정답입니다! 🎉" : "오답입니다.",
                "explanation", quiz.getExplanation() != null ? quiz.getExplanation() : "해설이 없습니다.",
                "correctAnswer", quiz.getCorrectAnswer()
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats(Long userId) {
        // ... (기존 코드 생략 - 변경 없음)
        int totalAttempts = quizAttemptRepository.countByUserId(userId);
        int correctCount = quizAttemptRepository.countByUserIdAndIsCorrectTrue(userId);
        int accuracy = totalAttempts == 0 ? 0 : (int)((double)correctCount / totalAttempts * 100);

        int createdCount = quizRepository.countByCreatorId(userId);

        return Map.of(
                "totalAttempts", totalAttempts,
                "accuracy", accuracy,
                "points", correctCount * 10,
                "createdCount", createdCount
        );
    }
}