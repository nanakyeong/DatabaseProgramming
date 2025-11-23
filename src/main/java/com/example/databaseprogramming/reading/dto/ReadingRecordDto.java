package com.example.databaseprogramming.reading.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ReadingRecordDto {
    private Long userId; // 프론트에서 보내줄 사용자 ID
    private String title;
    private String author;
    private Integer pages;
    private String status;
    private Integer rating;
    private String memo;
    private LocalDate readingDate;
}