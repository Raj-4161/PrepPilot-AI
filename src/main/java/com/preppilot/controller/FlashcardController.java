package com.preppilot.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.preppilot.dto.FlashcardResponse;
import com.preppilot.service.FlashcardService;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    private final FlashcardService flashcardService;

    public FlashcardController(
            FlashcardService flashcardService) {

        this.flashcardService = flashcardService;
    }

    // GENERATE FLASHCARDS
    @PostMapping("/documents/{documentId}/generate")
    public List<FlashcardResponse> generateFlashcards(
            @PathVariable Long documentId,
            Authentication authentication) {

        return flashcardService.generateFlashcards(
                documentId,
                authentication.getName()
        );
    }

    // GET FLASHCARDS OF A DOCUMENT
    @GetMapping("/document/{documentId}")
    public List<FlashcardResponse> getFlashcardsByDocument(
            @PathVariable Long documentId,
            Authentication authentication) {

        return flashcardService.getFlashcardsByDocument(
                documentId,
                authentication.getName()
        );
    }
}