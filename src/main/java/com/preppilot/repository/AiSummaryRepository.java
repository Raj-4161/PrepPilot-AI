package com.preppilot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.preppilot.entity.AiSummary;
import com.preppilot.entity.Document;

public interface AiSummaryRepository extends JpaRepository<AiSummary, Long> {

    Optional<AiSummary> findByDocument(Document document);
}