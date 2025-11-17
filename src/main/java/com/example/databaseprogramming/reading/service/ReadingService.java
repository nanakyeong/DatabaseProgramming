package com.example.databaseprogramming.reading.service;

import com.example.databaseprogramming.reading.dto.ReadingGoalDto;
import com.example.databaseprogramming.reading.dto.ReadingRecordDto;
import com.example.databaseprogramming.reading.dto.ReadingStatsDto;
import com.example.databaseprogramming.reading.entity.ReadingRecord;
import com.example.databaseprogramming.reading.entity.User;
import com.example.databaseprogramming.reading.repository.ReadingRecordRepository;
import com.example.databaseprogramming.reading.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadingService {
    
    private final ReadingRecordRepository readingRecordRepository;
    private final UserRepository userRepository;
    
    public List<ReadingRecordDto> getAllRecords() {
        return readingRecordRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ReadingRecordDto> getUserRecords(Long userId) {
        return readingRecordRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ReadingRecordDto createRecord(ReadingRecordDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ReadingRecord record = ReadingRecord.builder()
                .user(user)
                .bookTitle(dto.getBookTitle())
                .author(dto.getAuthor())
                .pagesRead(dto.getPagesRead())
                .status(dto.getStatus())
                .rating(dto.getRating())
                .memo(dto.getMemo())
                .readingDate(dto.getReadingDate())
                .build();
        
        ReadingRecord saved = readingRecordRepository.save(record);
        return convertToDto(saved);
    }
    
    public ReadingStatsDto getUserStats(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        
        Integer monthlyBooks = readingRecordRepository.countByUserIdAndReadingDateBetween(
                userId, startOfMonth, now);
        
        Integer consecutiveDays = calculateConsecutiveDays(userId);
        
        return ReadingStatsDto.builder()
                .monthlyBooks(monthlyBooks)
                .consecutiveDays(consecutiveDays)
                .quizAccuracy(87) // 임시값
                .totalRank(23) // 임시값
                .build();
    }
    
    public ReadingGoalDto getUserGoal(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1);
        
        Integer weeklyProgress = readingRecordRepository.countByUserIdAndReadingDateBetween(
                userId, startOfWeek, now);
        
        Integer weeklyGoal = 5;
        Integer percentage = (int) ((weeklyProgress / (double) weeklyGoal) * 100);
        
        return ReadingGoalDto.builder()
                .weeklyGoal(weeklyGoal)
                .currentProgress(weeklyProgress)
                .progressPercentage(percentage)
                .build();
    }
    
    private Integer calculateConsecutiveDays(Long userId) {
        List<ReadingRecord> records = readingRecordRepository
                .findByUserIdOrderByReadingDateDesc(userId);
        
        if (records.isEmpty()) return 0;
        
        int consecutive = 1;
        LocalDate currentDate = records.get(0).getReadingDate();
        
        for (int i = 1; i < records.size(); i++) {
            LocalDate prevDate = records.get(i).getReadingDate();
            if (currentDate.minusDays(1).equals(prevDate)) {
                consecutive++;
                currentDate = prevDate;
            } else {
                break;
            }
        }
        
        return consecutive;
    }
    
    private ReadingRecordDto convertToDto(ReadingRecord record) {
        return ReadingRecordDto.builder()
                .id(record.getId())
                .userId(record.getUser().getId())
                .bookTitle(record.getBookTitle())
                .author(record.getAuthor())
                .pagesRead(record.getPagesRead())
                .status(record.getStatus())
                .rating(record.getRating())
                .memo(record.getMemo())
                .readingDate(record.getReadingDate())
                .build();
    }
}
