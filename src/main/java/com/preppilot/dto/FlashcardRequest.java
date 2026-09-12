package com.preppilot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FlashcardRequest {

    @NotBlank(message = "Question is required")
    private String question;

    @NotBlank(message = "Answer is required")
    private String answer;
}