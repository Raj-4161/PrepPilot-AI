package com.preppilot.dto;

import lombok.Data;

@Data
public class SearchResultResponse {

    private String type;

    private Long id;

    private String title;

    private String description;
}