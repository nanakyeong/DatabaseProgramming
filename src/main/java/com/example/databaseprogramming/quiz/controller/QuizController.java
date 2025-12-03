package com.example.databaseprogramming.quiz.controller;

import com.example.databaseprogramming.quiz.dto.QuizDto;
import com.example.databaseprogramming.quiz.dto.QuizSolveRequest;
import com.example.databaseprogramming.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // 추가
import org.springframework.data.domain.PageRequest; // 추가
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 2. [수정] 퀴즈 목록 조회 (페이징)
    // 예: /api/quiz?page=0&size=6
    @GetMapping
    public ResponseEntity<Page<QuizDto>> getAllQuizzes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        // PageRequest.of(페이지번호, 사이즈) -> 0페이지부터 6개씩
        return ResponseEntity.ok(quizService.getAllQuizzes(PageRequest.of(page, size)));
    }

    // 3. 퀴즈 정답 제출 (기존 유지)
    @PostMapping("/solve")
    public ResponseEntity<?> solveQuiz(@RequestBody QuizSolveRequest request) {
        try {
            Map<String, Object> result = quizService.solveQuiz(request);
            return ResponseEntity.ok(Map.of("success", true, "result", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 4. 내 퀴즈 통계 (기존 유지)
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestParam Long userId) {
        return ResponseEntity.ok(quizService.getUserStats(userId));
    }
}