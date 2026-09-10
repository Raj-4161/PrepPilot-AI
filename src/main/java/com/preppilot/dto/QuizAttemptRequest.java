package com.preppilot.dto;

import java.util.List;

import lombok.Data;

@Data
public class QuizAttemptRequest {

    private List<QuizAnswerRequest> answers;
}
