package com.preppilot.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.preppilot.ai.GeminiService;
import com.preppilot.entity.AiSummary;
import com.preppilot.entity.Document;
import com.preppilot.entity.User;
import com.preppilot.repository.AiSummaryRepository;
import com.preppilot.repository.DocumentRepository;
import com.preppilot.repository.UserRepository;

@Service
public class AiSummaryService {

    private final DocumentRepository documentRepository;
    private final GeminiService geminiService;
    private final UserRepository userRepository;
    private final AiSummaryRepository aiSummaryRepository;

    public AiSummaryService(
            DocumentRepository documentRepository,
            GeminiService geminiService,
            UserRepository userRepository,
            AiSummaryRepository aiSummaryRepository) {

        this.documentRepository = documentRepository;
        this.geminiService = geminiService;
        this.userRepository = userRepository;
        this.aiSummaryRepository = aiSummaryRepository;
    }

    public String generateDocumentSummary(Long documentId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Check document ownership
        if (!document.getSubject().getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to access this document");
        }

        // Check if summary already exists
        var existingSummary =
                aiSummaryRepository.findByDocument(document);

        if (existingSummary.isPresent()) {
            return existingSummary.get().getSummary();
        }

        String extractedText = document.getExtractedText();

        // Check extracted text
        if (extractedText == null || extractedText.isBlank()) {
            throw new RuntimeException(
                    "No extracted text available for this document");
        }

        // Generate summary using Gemini
        String summary =
                geminiService.generateSummary(extractedText);

        // Save summary in database
        AiSummary aiSummary = new AiSummary();

        aiSummary.setDocument(document);
        aiSummary.setSummary(summary);
        aiSummary.setCreatedAt(LocalDateTime.now());

        aiSummaryRepository.save(aiSummary);

        return summary;
    }
}