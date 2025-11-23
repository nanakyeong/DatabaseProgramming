package com.example.databaseprogramming.ranking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankingDto {
    private Long userId;
    private String nickname;
    private int level;      // 레벨 (독서량 기반 계산)
    private int score;      // 독서량(권) 또는 퀴즈점수(점)
    private String scoreLabel; // "12권" 또는 "1500점" 등 화면 표시용
    private int rank;       // 등수
    private boolean isMe;   // 로그인한 본인인지 여부
}