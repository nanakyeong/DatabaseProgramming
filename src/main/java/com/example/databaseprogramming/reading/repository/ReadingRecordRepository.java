package com.example.databaseprogramming.reading.repository;

import com.example.databaseprogramming.reading.entity.ReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    
    List<ReadingRecord> findByUserId(Long userId);
    
    List<ReadingRecord> findByUserIdOrderByReadingDateDesc(Long userId);
    
    Integer countByUserIdAndReadingDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
