package com.example.databaseprogramming.quiz.controller;

import com.example.databaseprogramming.quiz.dto.CommentDto;
import com.example.databaseprogramming.quiz.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@RequestParam Long quizId) {
        return ResponseEntity.ok(commentService.getComments(quizId));
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Map<String, Object> body) {
        Long quizId = ((Number) body.get("quizId")).longValue();
        Long userId = ((Number) body.get("userId")).longValue();
        String content = (String) body.get("content");

        commentService.addComment(quizId, userId, content);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/like")
    public ResponseEntity<?> toggleLike(@RequestBody Map<String, Object> body) {
        Long quizId = ((Number) body.get("quizId")).longValue();
        Long userId = ((Number) body.get("userId")).longValue();

        return ResponseEntity.ok(commentService.toggleLike(quizId, userId));
    }

    @GetMapping("/like")
    public ResponseEntity<?> getLikeStatus(@RequestParam Long quizId, @RequestParam Long userId) {
        return ResponseEntity.ok(commentService.getLikeStatus(quizId, userId));
    }
}