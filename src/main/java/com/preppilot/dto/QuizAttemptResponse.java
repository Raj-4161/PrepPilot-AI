package com.preppilot.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptResponse {

    private Long attemptId;
    private Long quizId;
    private Integer score;
    private Integer totalQuestions;
    private Double percentage;
    private String message;
    private LocalDateTime attemptedAt;
    private List<AnswerResult> answers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerResult {
        private Long questionId;
        private String question;
        private String selectedAnswer;
        private String correctAnswer;
        private Boolean correct;
    }
}