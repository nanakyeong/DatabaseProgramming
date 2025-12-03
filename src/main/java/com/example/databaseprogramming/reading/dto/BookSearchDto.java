package com.example.databaseprogramming.reading.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookSearchDto {
    private String title;
    private String author;
    private String coverUrl; // 책 표지 이미지 URL
    private String publisher;
    private String isbn;
}