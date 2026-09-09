package com.preppilot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.preppilot.entity.Document;
import com.preppilot.entity.Subject;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findBySubject(Subject subject);
}