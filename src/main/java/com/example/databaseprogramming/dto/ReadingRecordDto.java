package com.example.databaseprogramming.dto;

import com.example.databaseprogramming.entity.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingRecordDto {
    private Long id;
    private Long userId;
    private String bookTitle;
    private String author;
    private Integer pagesRead;
    private ReadingStatus status;
    private Integer rating;
    private String memo;
    private LocalDate readingDate;
}
