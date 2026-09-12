package com.preppilot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.preppilot.entity.Document;
import com.preppilot.entity.Flashcard;
import com.preppilot.entity.Flashcard;
import com.preppilot.entity.User;

public interface FlashcardRepository
        extends JpaRepository<Flashcard, Long> {

    List<Flashcard> findByDocument(Document document);

    long countByDocument_Subject_User(User user);

    List<Flashcard> findByDocument_Subject_UserAndQuestionContainingIgnoreCase(
            User user,
            String question);
}