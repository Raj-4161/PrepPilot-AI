package com.preppilot.dto;

import lombok.Data;

@Data
public class QuizAttemptResponse {

    private Long attemptId;

    private Long quizId;

    private Integer score;

    private Integer totalQuestions;

    private Double percentage;

    private String message;
}