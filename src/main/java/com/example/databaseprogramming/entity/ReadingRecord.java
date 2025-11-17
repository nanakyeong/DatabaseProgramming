package com.example.databaseprogramming.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false)
    private String bookTitle;
    
    private String author;
    
    private Integer pagesRead;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;
    
    private Integer rating;
    
    @Column(length = 500)
    private String memo;
    
    @Column(nullable = false)
    private LocalDate readingDate;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
