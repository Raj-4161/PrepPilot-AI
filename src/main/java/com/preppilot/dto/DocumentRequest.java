package com.preppilot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DocumentRequest {

    @NotBlank(message = "Document title is required")
    @Size(max = 200, message = "Document title must not exceed 200 characters")
    private String title;
}