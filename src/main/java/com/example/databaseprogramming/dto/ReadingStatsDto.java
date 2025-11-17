package com.example.databaseprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingStatsDto {
    private Integer monthlyBooks;
    private Integer consecutiveDays;
    private Integer quizAccuracy;
    private Integer totalRank;
}
