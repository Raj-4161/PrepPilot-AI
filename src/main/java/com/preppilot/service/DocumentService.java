package com.preppilot.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.preppilot.dto.DocumentResponse;
import com.preppilot.entity.Document;
import com.preppilot.entity.Subject;
import com.preppilot.entity.User;
import com.preppilot.repository.DocumentRepository;
import com.preppilot.repository.SubjectRepository;
import com.preppilot.repository.UserRepository;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final PdfTextExtractorService pdfTextExtractorService;

    private final Path uploadDirectory = Paths.get("uploads");

    public DocumentService(
            DocumentRepository documentRepository,
            SubjectRepository subjectRepository,
            UserRepository userRepository,
            PdfTextExtractorService pdfTextExtractorService) {

        this.documentRepository = documentRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.pdfTextExtractorService = pdfTextExtractorService;
    }

    public DocumentResponse uploadDocument(
            Long subjectId,
            String title,
            MultipartFile file,
            String email) throws IOException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to upload to this subject");
        }

        if (file.isEmpty()) {
            throw new RuntimeException("File cannot be empty");
        }

        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        Files.createDirectories(uploadDirectory);

        String originalFileName = file.getOriginalFilename();

        String storedFileName =
                System.currentTimeMillis() + "_" + originalFileName;

        Path filePath = uploadDirectory.resolve(storedFileName);

        Files.copy(file.getInputStream(), filePath);

        // Extract text from PDF
        String extractedText =
                pdfTextExtractorService.extractText(file.getBytes());

        // Temporary debugging
        System.out.println("========== PDF EXTRACTION ==========");
        System.out.println("Extracted text length: " + extractedText.length());
        System.out.println(
                extractedText.substring(
                        0,
                        Math.min(500, extractedText.length())
                )
        );
        System.out.println("====================================");

        Document document = new Document();

        document.setTitle(title);
        document.setOriginalFileName(originalFileName);
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setFilePath(filePath.toString());
        document.setExtractedText(extractedText);
        document.setUploadedAt(LocalDateTime.now());
        document.setSubject(subject);

        Document savedDocument = documentRepository.save(document);

        return convertToResponse(savedDocument);
    }

    public List<DocumentResponse> getDocumentsBySubject(
            Long subjectId,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to access this subject");
        }

        List<Document> documents =
                documentRepository.findBySubject(subject);

        return documents.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private DocumentResponse convertToResponse(Document document) {

        DocumentResponse response = new DocumentResponse();

        response.setId(document.getId());
        response.setTitle(document.getTitle());
        response.setOriginalFileName(document.getOriginalFileName());
        response.setFileType(document.getFileType());
        response.setFileSize(document.getFileSize());
        response.setUploadedAt(document.getUploadedAt());
        response.setSubjectId(document.getSubject().getId());

        return response;
    }
}