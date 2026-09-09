package com.preppilot.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DocumentResponse {

    private Long id;
    private String title;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private Long subjectId;
}