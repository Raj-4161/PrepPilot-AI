package com.preppilot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_summaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private Document document;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private LocalDateTime createdAt;
}