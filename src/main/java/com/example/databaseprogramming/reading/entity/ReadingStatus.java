package com.example.databaseprogramming.reading.entity;

public enum ReadingStatus {
    READING("읽는 중"),
    COMPLETED("완독"),
    STARTED("읽기 시작");
    
    private final String description;
    
    ReadingStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
