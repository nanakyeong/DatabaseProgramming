package com.example.databaseprogramming.reading.service;

import com.example.databaseprogramming.login.entity.User;
import com.example.databaseprogramming.login.repository.UserRepository;
import com.example.databaseprogramming.reading.dto.DashboardStatsDto;
import com.example.databaseprogramming.reading.dto.ReadingRecordDto;
import com.example.databaseprogramming.reading.entity.ReadingRecord;
import com.example.databaseprogramming.reading.repository.ReadingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReadingService {

    private final ReadingRecordRepository readingRecordRepository;
    private final UserRepository userRepository;

    // 저장 (기존과 동일)
    @Transactional
    public void saveRecord(ReadingRecordDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 해당 날짜에 이미 기록이 있다면 덮어쓰기 혹은 에러 처리 (여기선 덮어쓰기 방식으로 수정 호출)
        // 간단하게 신규 생성만 처리
        ReadingRecord record = ReadingRecord.builder()
                .user(user)
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .pages(dto.getPages())
                .status(dto.getStatus())
                .rating(dto.getRating())
                .memo(dto.getMemo())
                .readingDate(dto.getReadingDate())
                .build();
        readingRecordRepository.save(record);
    }

    // 상세 조회 (모달 열 때 사용)
    @Transactional(readOnly = true)
    public ReadingRecordDto getRecordByDate(Long userId, LocalDate date) {
        return readingRecordRepository.findByUserIdAndReadingDate(userId, date)
                .map(record -> ReadingRecordDto.builder()
                        .title(record.getTitle())
                        .author(record.getAuthor())
                        .pages(record.getPages())
                        .status(record.getStatus())
                        .rating(record.getRating())
                        .memo(record.getMemo())
                        .readingDate(record.getReadingDate())
                        .build())
                .orElse(null); // 기록이 없으면 null 반환
    }

    // 수정
    @Transactional
    public void updateRecord(ReadingRecordDto dto) {
        ReadingRecord record = readingRecordRepository.findByUserIdAndReadingDate(dto.getUserId(), dto.getReadingDate())
                .orElseThrow(() -> new IllegalArgumentException("수정할 기록을 찾을 수 없습니다."));

        // Dirty Checking으로 자동 업데이트
        record.setTitle(dto.getTitle());
        record.setAuthor(dto.getAuthor());
        record.setPages(dto.getPages());
        record.setStatus(dto.getStatus());
        record.setRating(dto.getRating());
        record.setMemo(dto.getMemo());
    }

    // 삭제
    @Transactional
    public void deleteRecord(Long userId, LocalDate date) {
        ReadingRecord record = readingRecordRepository.findByUserIdAndReadingDate(userId, date)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 기록을 찾을 수 없습니다."));

        readingRecordRepository.delete(record);
    }

    // 캘린더 데이터 조회 (기존과 동일)
    @Transactional(readOnly = true)
    public Map<String, String> getMonthlyCalendarData(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<ReadingRecord> records = readingRecordRepository.findByUserIdAndReadingDateBetween(userId, startDate, endDate);
        Map<String, String> calendarData = new HashMap<>();

        for (ReadingRecord record : records) {
            String dateKey = record.getReadingDate().getYear() + "-" +
                    record.getReadingDate().getMonthValue() + "-" +
                    record.getReadingDate().getDayOfMonth();
            calendarData.put(dateKey, mapStatusToClass(record.getStatus()));
        }
        return calendarData;
    }

    private String mapStatusToClass(String status) {
        switch (status) {
            case "완독": return "reading-complete";
            case "읽는 중": return "reading-medium";
            default: return "reading-light";
        }
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats(Long userId) {
        LocalDate today = LocalDate.now();

        // 1. 이번 달 독서량
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        List<ReadingRecord> monthRecords = readingRecordRepository.findByUserIdAndReadingDateBetween(userId, startOfMonth, endOfMonth);
        int monthlyCount = monthRecords.size();

        // 2. 연속 독서일 계산
        List<LocalDate> dates = readingRecordRepository.findDistinctReadingDates(userId);
        int consecutiveDays = calculateConsecutiveDays(dates, today);

        // 3. 이번 주 목표 달성 (월요일~일요일 기준)
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1); // 월요일
        LocalDate endOfWeek = startOfWeek.plusDays(6); // 일요일
        List<ReadingRecord> weekRecords = readingRecordRepository.findByUserIdAndReadingDateBetween(userId, startOfWeek, endOfWeek);
        int weeklyCount = weekRecords.size();

        // 4. 최근 기록 3건
        List<ReadingRecord> recentEntities = readingRecordRepository.findTop3ByUserIdOrderByReadingDateDesc(userId);
        List<ReadingRecordDto> recentDtos = recentEntities.stream()
                .map(r -> ReadingRecordDto.builder()
                        .title(r.getTitle())
                        .author(r.getAuthor())
                        .pages(r.getPages())
                        .status(r.getStatus())
                        .readingDate(r.getReadingDate())
                        .build())
                .collect(java.util.stream.Collectors.toList());

        return DashboardStatsDto.builder()
                .monthlyCount(monthlyCount)
                .consecutiveDays(consecutiveDays)
                .weeklyGoalAchieved(weeklyCount)
                .weeklyGoalTarget(5) // 목표는 일단 5권으로 고정 (나중에 설정 기능 추가 가능)
                .recentRecords(recentDtos)
                .quizAccuracy(0) // 아직 구현 안됨
                .totalRanking(0) // 아직 구현 안됨
                .build();
    }

    // 연속일 계산 알고리즘
    private int calculateConsecutiveDays(List<LocalDate> sortedDates, LocalDate today) {
        if (sortedDates.isEmpty()) return 0;

        int count = 0;
        LocalDate checkDate = today;

        // 만약 오늘 기록이 없으면, 어제부터 기록이 있는지 확인 (오늘 안 읽었어도 연속일이 끊기진 않은 상태로 볼지 여부 결정)
        // 여기서는 "오늘 기록이 없으면 어제부터 체크"하는 방식으로 구현 (유연한 방식)
        if (!sortedDates.contains(today)) {
            checkDate = today.minusDays(1);
        }

        for (LocalDate date : sortedDates) {
            if (date.equals(checkDate)) {
                count++;
                checkDate = checkDate.minusDays(1);
            } else if (date.isBefore(checkDate)) {
                // 날짜가 건너뛰어지면 연속 종료
                break;
            }
        }
        return count;
    }
}