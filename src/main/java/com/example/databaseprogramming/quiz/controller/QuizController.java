package com.example.databaseprogramming.quiz.controller;

import com.example.databaseprogramming.quiz.dto.QuizDto;
import com.example.databaseprogramming.quiz.dto.QuizSolveRequest;
import com.example.databaseprogramming.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;

    // 1. 퀴즈 생성
    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody QuizDto dto) {
        try {
            quizService.createQuiz(dto);
            return ResponseEntity.ok(Map.of("success", true, "message", "퀴즈가 등록되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 2. 퀴즈 목록 조회
    @GetMapping
    public ResponseEntity<List<QuizDto>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    // 3. 퀴즈 정답 제출
    @PostMapping("/solve")
    public ResponseEntity<?> solveQuiz(@RequestBody QuizSolveRequest request) {
        try {
            Map<String, Object> result = quizService.solveQuiz(request);
            return ResponseEntity.ok(Map.of("success", true, "result", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 4. 내 퀴즈 통계
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestParam Long userId) {
        return ResponseEntity.ok(quizService.getUserStats(userId));
    }
}