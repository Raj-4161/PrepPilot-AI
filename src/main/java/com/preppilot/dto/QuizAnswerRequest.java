package com.preppilot.dto;

import lombok.Data;

@Data
public class QuizAnswerRequest {

    private Long questionId;

    private String selectedAnswer;
}