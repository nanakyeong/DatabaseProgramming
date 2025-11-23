package com.example.databaseprogramming.reading.repository;

import com.example.databaseprogramming.reading.entity.ReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    // 기존 메소드들 유지...
    List<ReadingRecord> findByUserIdAndReadingDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    java.util.Optional<ReadingRecord> findByUserIdAndReadingDate(Long userId, LocalDate readingDate);

    // [추가] 특정 사용자의 모든 독서 날짜를 최신순으로 가져오기 (연속일 계산용)
    @Query("SELECT DISTINCT r.readingDate FROM ReadingRecord r WHERE r.user.id = :userId ORDER BY r.readingDate DESC")
    List<LocalDate> findDistinctReadingDates(@Param("userId") Long userId);

    // [추가] 최근 독서 기록 3개 가져오기
    List<ReadingRecord> findTop3ByUserIdOrderByReadingDateDesc(Long userId);

    int countByUserIdAndStatus(Long userId, String status);
}