package com.preppilot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.preppilot.ai.GeminiService;
import com.preppilot.dto.FlashcardResponse;
import com.preppilot.entity.Document;
import com.preppilot.entity.Flashcard;
import com.preppilot.entity.User;
import com.preppilot.repository.DocumentRepository;
import com.preppilot.repository.FlashcardRepository;
import com.preppilot.repository.UserRepository;

@Service
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final FlashcardParserService flashcardParserService;

    public FlashcardService(
            FlashcardRepository flashcardRepository,
            DocumentRepository documentRepository,
            UserRepository userRepository,
            GeminiService geminiService,
            FlashcardParserService flashcardParserService) {

        this.flashcardRepository = flashcardRepository;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.geminiService = geminiService;
        this.flashcardParserService = flashcardParserService;
    }

    @Transactional
    public List<FlashcardResponse> generateFlashcards(
            Long documentId,
            String email) {

        // 1. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 2. Find document
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new RuntimeException("Document not found"));

        // 3. Check document ownership
        if (!document.getSubject()
                .getUser()
                .getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to access this document");
        }

        // 4. Get extracted text
        String extractedText =
                document.getExtractedText();

        if (extractedText == null ||
                extractedText.isBlank()) {

            throw new RuntimeException(
                    "No extracted text available for this document");
        }

        // 5. Generate flashcards using Gemini
        String flashcardText =
                geminiService.generateFlashcards(
                        extractedText);

        // 6. Parse Gemini response
        List<FlashcardResponse> parsedFlashcards =
                flashcardParserService.parseFlashcards(
                        flashcardText);

        // 7. Save flashcards
        List<FlashcardResponse> savedFlashcards =
                new ArrayList<>();

        for (FlashcardResponse flashcardResponse
                : parsedFlashcards) {

            Flashcard flashcard =
                    new Flashcard();

            flashcard.setDocument(document);

            flashcard.setQuestion(
                    flashcardResponse.getQuestion());

            flashcard.setAnswer(
                    flashcardResponse.getAnswer());

            Flashcard savedFlashcard =
                    flashcardRepository.save(
                            flashcard);

            // 8. Convert saved entity to response
            FlashcardResponse response =
                    new FlashcardResponse();

            response.setId(
                    savedFlashcard.getId());

            response.setDocumentId(
                    document.getId());

            response.setQuestion(
                    savedFlashcard.getQuestion());

            response.setAnswer(
                    savedFlashcard.getAnswer());

            savedFlashcards.add(response);
        }

        return savedFlashcards;
    }

    public List<FlashcardResponse> getFlashcardsByDocument(
            Long documentId,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new RuntimeException("Document not found"));

        // Ownership check
        if (!document.getSubject()
                .getUser()
                .getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to access this document");
        }

        List<Flashcard> flashcards =
                flashcardRepository.findByDocument(
                        document);

        List<FlashcardResponse> responses =
                new ArrayList<>();

        for (Flashcard flashcard : flashcards) {

            FlashcardResponse response =
                    new FlashcardResponse();

            response.setId(
                    flashcard.getId());

            response.setDocumentId(
                    document.getId());

            response.setQuestion(
                    flashcard.getQuestion());

            response.setAnswer(
                    flashcard.getAnswer());

            responses.add(response);
        }

        return responses;
    }
}