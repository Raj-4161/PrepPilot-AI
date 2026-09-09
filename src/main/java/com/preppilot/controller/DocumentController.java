package com.preppilot.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.preppilot.dto.DocumentResponse;
import com.preppilot.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(
            value = "/subject/{subjectId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public DocumentResponse uploadDocument(
            @PathVariable Long subjectId,
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        return documentService.uploadDocument(
                subjectId,
                title,
                file,
                authentication.getName()
        );
    }

    @GetMapping("/subject/{subjectId}")
    public List<DocumentResponse> getDocumentsBySubject(
            @PathVariable Long subjectId,
            Authentication authentication) {

        return documentService.getDocumentsBySubject(
                subjectId,
                authentication.getName()
        );
    }
}