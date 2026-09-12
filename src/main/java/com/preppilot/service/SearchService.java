package com.preppilot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.preppilot.dto.SearchResultResponse;
import com.preppilot.entity.Document;
import com.preppilot.entity.Flashcard;
import com.preppilot.entity.User;
import com.preppilot.repository.DocumentRepository;
import com.preppilot.repository.FlashcardRepository;
import com.preppilot.repository.UserRepository;

@Service
public class SearchService {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final FlashcardRepository flashcardRepository;

    public SearchService(
            UserRepository userRepository,
            DocumentRepository documentRepository,
            FlashcardRepository flashcardRepository) {

        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.flashcardRepository = flashcardRepository;
    }

    public List<SearchResultResponse> search(
            String query,
            String email) {

        // 1. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 2. Validate search query
        if (query == null || query.isBlank()) {
            throw new RuntimeException(
                    "Search query cannot be empty");
        }

        String searchQuery = query.trim();

        List<SearchResultResponse> results =
                new ArrayList<>();

        // 3. Search documents
        List<Document> documents =
                documentRepository
                        .findBySubject_UserAndTitleContainingIgnoreCase(
                                user,
                                searchQuery);

        for (Document document : documents) {

            SearchResultResponse response =
                    new SearchResultResponse();

            response.setType("DOCUMENT");

            response.setId(document.getId());

            response.setTitle(
                    document.getTitle());

            response.setDescription(
                    document.getOriginalFileName());

            results.add(response);
        }

        // 4. Search flashcards
        List<Flashcard> flashcards =
                flashcardRepository
                        .findByDocument_Subject_UserAndQuestionContainingIgnoreCase(
                                user,
                                searchQuery);

        for (Flashcard flashcard : flashcards) {

            SearchResultResponse response =
                    new SearchResultResponse();

            response.setType("FLASHCARD");

            response.setId(flashcard.getId());

            response.setTitle(
                    flashcard.getQuestion());

            response.setDescription(
                    flashcard.getAnswer());

            results.add(response);
        }

        return results;
    }
}