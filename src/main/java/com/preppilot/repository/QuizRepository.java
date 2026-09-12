package com.preppilot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.preppilot.entity.Quiz;
import com.preppilot.entity.Document;
import com.preppilot.entity.User;

import java.util.List;

public interface QuizRepository
        extends JpaRepository<Quiz, Long> {

    List<Quiz> findByDocument(Document document);

    long countByDocument_Subject_User(User user);
}