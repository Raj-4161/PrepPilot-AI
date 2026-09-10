package com.preppilot.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class QuizResponse {

    private Long id;

    private Long documentId;

    private String title;

    private LocalDateTime createdAt;

    private List<QuizQuestionResponse> questions;
}