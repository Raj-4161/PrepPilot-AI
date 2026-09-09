package com.preppilot.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.preppilot.service.AiSummaryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;

import com.preppilot.ai.GeminiService;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final GeminiService geminiService;
    private final AiSummaryService aiSummaryService;

    public AiController(
            GeminiService geminiService,
            AiSummaryService aiSummaryService) {

        this.geminiService = geminiService;
        this.aiSummaryService = aiSummaryService;
    }

    @PostMapping("/test")
    public Map<String, String> testGemini(
            @RequestBody Map<String, String> request) {

        String text = request.get("text");

        String summary = geminiService.generateSummary(text);

        return Map.of(
                "summary", summary
        );
    }
    
    @PostMapping("/documents/{documentId}/summary")
    public Map<String, String> generateSummary(
            @PathVariable Long documentId,
            Authentication authentication) {

        String summary = aiSummaryService.generateDocumentSummary(
                documentId,
                authentication.getName()
        );

        return Map.of(
                "summary", summary
        );
    }
}