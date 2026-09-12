package com.preppilot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.preppilot.entity.Document;
import com.preppilot.entity.Subject;
import com.preppilot.entity.User;

public interface DocumentRepository
        extends JpaRepository<Document, Long> {

    List<Document> findBySubject(Subject subject);

    long countBySubject_User(User user);

    List<Document> findBySubject_UserAndTitleContainingIgnoreCase(
            User user,
            String title);
}