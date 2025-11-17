package com.example.databaseprogramming.reading.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingGoalDto {
    private Integer weeklyGoal;
    private Integer currentProgress;
    private Integer progressPercentage;
}
