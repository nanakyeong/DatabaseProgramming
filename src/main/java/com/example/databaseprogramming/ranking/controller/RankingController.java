package com.example.databaseprogramming.ranking.controller;

import com.example.databaseprogramming.ranking.dto.RankingDto;
import com.example.databaseprogramming.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<List<RankingDto>> getRanking(
            @RequestParam String type, // "reading" or "quiz"
            @RequestParam Long userId) {

        return ResponseEntity.ok(rankingService.getRanking(type, userId));
    }
}