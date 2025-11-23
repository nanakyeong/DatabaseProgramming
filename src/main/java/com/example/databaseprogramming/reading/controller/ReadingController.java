package com.example.databaseprogramming.reading.controller;

import com.example.databaseprogramming.reading.dto.ReadingRecordDto;
import com.example.databaseprogramming.reading.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reading")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReadingController {

    private final ReadingService readingService;

    // 1. 저장 (POST)
    @PostMapping
    public ResponseEntity<?> saveRecord(@RequestBody ReadingRecordDto dto) {
        try {
            readingService.saveRecord(dto);
            return ResponseEntity.ok(Map.of("success", true, "message", "저장되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 2. 수정 (PUT)
    @PutMapping
    public ResponseEntity<?> updateRecord(@RequestBody ReadingRecordDto dto) {
        try {
            readingService.updateRecord(dto);
            return ResponseEntity.ok(Map.of("success", true, "message", "수정되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 3. 삭제 (DELETE)
    @DeleteMapping
    public ResponseEntity<?> deleteRecord(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            readingService.deleteRecord(userId, date);
            return ResponseEntity.ok(Map.of("success", true, "message", "삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 4. 상세 조회 (모달 오픈용)
    @GetMapping("/detail")
    public ResponseEntity<?> getRecordDetail(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ReadingRecordDto record = readingService.getRecordByDate(userId, date);
        return ResponseEntity.ok(Map.of("success", true, "data", record)); // record가 null이어도 data: null로 전송
    }

    // 5. 캘린더 데이터 조회
    @GetMapping("/calendar")
    public ResponseEntity<?> getCalendarData(
            @RequestParam Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, String> data = readingService.getMonthlyCalendarData(userId, year, month);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(@RequestParam Long userId) {
        return ResponseEntity.ok(readingService.getDashboardStats(userId));
    }
}