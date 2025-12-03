package com.example.databaseprogramming.reading.entity;

import com.example.databaseprogramming.login.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reading_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자

    @Column(nullable = false)
    private String title; // 책 제목

    private String author; // 저자

    private Integer pages; // 읽은 페이지 수

    @Column(nullable = false)
    private String status; // 읽은 상태 (READING, COMPLETED, STARTED 등)

    private Integer rating; // 별점 (1~5)

    @Column(length = 500)
    private String memo; // 독서 메모

    @Column(nullable = false)
    private LocalDate readingDate; // 독서 날짜 (캘린더 표시용)

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(length = 1000)
    private String coverUrl;
}