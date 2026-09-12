package com.preppilot.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.preppilot.dto.SearchResultResponse;
import com.preppilot.service.SearchService;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(
            SearchService searchService) {

        this.searchService = searchService;
    }

    @GetMapping
    public List<SearchResultResponse> search(
            @RequestParam String q,
            Authentication authentication) {

        return searchService.search(
                q,
                authentication.getName()
        );
    }
}