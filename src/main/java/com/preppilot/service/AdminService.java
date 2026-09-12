package com.preppilot.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.preppilot.dto.AdminDashboardResponse;
import com.preppilot.dto.AdminUserResponse;
import com.preppilot.entity.AiSummary;
import com.preppilot.entity.Document;
import com.preppilot.entity.Flashcard;
import com.preppilot.entity.Quiz;
import com.preppilot.entity.QuizAnswer;
import com.preppilot.entity.QuizAttempt;
import com.preppilot.entity.QuizQuestion;
import com.preppilot.entity.Subject;
import com.preppilot.entity.User;
import com.preppilot.repository.AiSummaryRepository;
import com.preppilot.repository.DocumentRepository;
import com.preppilot.repository.FlashcardRepository;
import com.preppilot.repository.QuizAnswerRepository;
import com.preppilot.repository.QuizAttemptRepository;
import com.preppilot.repository.QuizQuestionRepository;
import com.preppilot.repository.QuizRepository;
import com.preppilot.repository.SubjectRepository;
import com.preppilot.repository.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final DocumentRepository documentRepository;
    private final AiSummaryRepository aiSummaryRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final FlashcardRepository flashcardRepository;

    public AdminService(
            UserRepository userRepository,
            SubjectRepository subjectRepository,
            DocumentRepository documentRepository,
            AiSummaryRepository aiSummaryRepository,
            QuizRepository quizRepository,
            QuizQuestionRepository quizQuestionRepository,
            QuizAttemptRepository quizAttemptRepository,
            QuizAnswerRepository quizAnswerRepository,
            FlashcardRepository flashcardRepository) {

        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.documentRepository = documentRepository;
        this.aiSummaryRepository = aiSummaryRepository;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.flashcardRepository = flashcardRepository;
    }

    public AdminDashboardResponse getDashboard() {

        long totalUsers = userRepository.count();

        long totalAdmins = userRepository.countByRole("ADMIN");

        long totalRegularUsers = userRepository.countByRole("USER");

        return new AdminDashboardResponse(
                totalUsers,
                totalAdmins,
                totalRegularUsers
        );
    }

    public List<AdminUserResponse> getAllUsers() {

        List<User> users =
                userRepository.findAllByOrderByCreatedAtDesc();

        List<AdminUserResponse> responses = new ArrayList<>();

        for (User user : users) {

            responses.add(
                    new AdminUserResponse(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole(),
                            user.getCreatedAt()
                    )
            );
        }

        return responses;
    }

    public AdminUserResponse getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public AdminUserResponse updateUserRole(
            Long userId,
            String role) {

        if (role == null || role.isBlank()) {
            throw new RuntimeException("Role is required");
        }

        role = role.trim().toUpperCase();

        if (!role.equals("USER") && !role.equals("ADMIN")) {
            throw new RuntimeException(
                    "Role must be USER or ADMIN"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        user.setRole(role);

        User updatedUser = userRepository.save(user);

        return new AdminUserResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getRole(),
                updatedUser.getCreatedAt()
        );
    }

    @Transactional
    public void deleteUser(
            Long userId,
            String adminEmail) {

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(
                        () -> new RuntimeException("Admin user not found")
                );

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        // Prevent admin from deleting their own account
        if (admin.getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You cannot delete your own admin account"
            );
        }

        // Prevent deletion of the last admin account
        if ("ADMIN".equals(user.getRole())
                && userRepository.countByRole("ADMIN") <= 1) {

            throw new RuntimeException(
                    "The last admin account cannot be deleted"
            );
        }

        // Get user's subjects
        List<Subject> subjects =
                subjectRepository.findByUser(user);

        for (Subject subject : subjects) {

            // Get subject documents
            List<Document> documents =
                    documentRepository.findBySubject(subject);

            for (Document document : documents) {

                // Delete AI summary
                aiSummaryRepository.findByDocument(document)
                        .ifPresent(aiSummary ->
                                aiSummaryRepository.delete(aiSummary)
                        );

                // Delete flashcards
                List<Flashcard> flashcards =
                        flashcardRepository.findByDocument(document);

                if (!flashcards.isEmpty()) {
                    flashcardRepository.deleteAll(flashcards);
                }

                // Get quizzes
                List<Quiz> quizzes =
                        quizRepository.findByDocument(document);

                for (Quiz quiz : quizzes) {

                    // Delete attempts and their answers
                    List<QuizAttempt> attempts =
                            quizAttemptRepository.findByQuiz(quiz);

                    for (QuizAttempt attempt : attempts) {

                        List<QuizAnswer> answers =
                                quizAnswerRepository.findByAttempt(attempt);

                        if (!answers.isEmpty()) {
                            quizAnswerRepository.deleteAll(answers);
                        }

                        quizAttemptRepository.delete(attempt);
                    }

                    // Delete quiz questions
                    List<QuizQuestion> questions =
                            quizQuestionRepository.findByQuiz(quiz);

                    if (!questions.isEmpty()) {
                        quizQuestionRepository.deleteAll(questions);
                    }

                    // Delete quiz
                    quizRepository.delete(quiz);
                }

                // Delete physical uploaded file
                deletePhysicalFile(document.getFilePath());

                // Delete document
                documentRepository.delete(document);
            }

            // Delete subject
            subjectRepository.delete(subject);
        }

        // Finally delete user
        userRepository.delete(user);
    }

    private void deletePhysicalFile(String filePath) {

        if (filePath == null || filePath.isBlank()) {
            return;
        }

        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (Exception exception) {
            // Database deletion should not fail if physical file
            // is already missing or cannot be deleted.
        }
    }
}