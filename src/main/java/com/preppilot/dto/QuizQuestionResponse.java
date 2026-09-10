package com.preppilot.dto;

import lombok.Data;

@Data
public class QuizQuestionResponse {

    private Long id;

    private String question;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    private String correctAnswer;
}