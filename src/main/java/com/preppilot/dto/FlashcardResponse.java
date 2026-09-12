package com.preppilot.dto;

import lombok.Data;

@Data
public class FlashcardResponse {

    private Long id;

    private Long documentId;

    private String question;

    private String answer;
}