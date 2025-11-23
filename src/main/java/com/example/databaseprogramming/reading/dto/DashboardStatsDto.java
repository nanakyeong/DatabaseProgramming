package com.example.databaseprogramming.reading.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardStatsDto {
    private int monthlyCount;      // 이번 달 독서량
    private int consecutiveDays;   // 연속 독서일
    private int weeklyGoalAchieved;// 이번 주 읽은 권수
    private int weeklyGoalTarget;  // 이번 주 목표 (기본 5권)
    private List<ReadingRecordDto> recentRecords; // 최근 기록 리스트

    // 퀴즈와 랭킹은 아직 DB가 없으므로 임시로 0 처리
    private int quizAccuracy;
    private int totalRanking;
}