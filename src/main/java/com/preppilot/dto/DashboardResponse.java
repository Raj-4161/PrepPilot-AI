package com.preppilot.dto;

import lombok.Data;

@Data
public class DashboardResponse {

    private Long totalSubjects;

    private Long totalDocuments;

    private Long totalQuizzes;

    private Long totalFlashcards;

    private Long totalAttempts;

    private Double averageScore;
}