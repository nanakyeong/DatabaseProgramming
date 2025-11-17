package com.example.databaseprogramming.controller;

import com.example.databaseprogramming.dto.ReadingRecordDto;
import com.example.databaseprogramming.dto.ReadingStatsDto;
import com.example.databaseprogramming.dto.ReadingGoalDto;
import com.example.databaseprogramming.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reading")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReadingController {
    
    private final ReadingService readingService;
    
    @GetMapping("/records")
    public ResponseEntity<List<ReadingRecordDto>> getReadingRecords() {
        return ResponseEntity.ok(readingService.getAllRecords());
    }
    
    @GetMapping("/records/{userId}")
    public ResponseEntity<List<ReadingRecordDto>> getUserReadingRecords(@PathVariable Long userId) {
        return ResponseEntity.ok(readingService.getUserRecords(userId));
    }
    
    @PostMapping("/records")
    public ResponseEntity<ReadingRecordDto> createReadingRecord(@RequestBody ReadingRecordDto recordDto) {
        return ResponseEntity.ok(readingService.createRecord(recordDto));
    }
    
    @GetMapping("/stats/{userId}")
    public ResponseEntity<ReadingStatsDto> getUserStats(@PathVariable Long userId) {
        return ResponseEntity.ok(readingService.getUserStats(userId));
    }
    
    @GetMapping("/goal/{userId}")
    public ResponseEntity<ReadingGoalDto> getUserGoal(@PathVariable Long userId) {
        return ResponseEntity.ok(readingService.getUserGoal(userId));
    }
}
