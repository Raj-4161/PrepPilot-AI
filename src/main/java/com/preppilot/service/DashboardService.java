package com.preppilot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.preppilot.dto.DashboardResponse;
import com.preppilot.entity.QuizAttempt;
import com.preppilot.entity.User;
import com.preppilot.repository.DocumentRepository;
import com.preppilot.repository.FlashcardRepository;
import com.preppilot.repository.QuizAttemptRepository;
import com.preppilot.repository.QuizRepository;
import com.preppilot.repository.SubjectRepository;
import com.preppilot.repository.UserRepository;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final DocumentRepository documentRepository;
    private final QuizRepository quizRepository;
    private final FlashcardRepository flashcardRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public DashboardService(
            UserRepository userRepository,
            SubjectRepository subjectRepository,
            DocumentRepository documentRepository,
            QuizRepository quizRepository,
            FlashcardRepository flashcardRepository,
            QuizAttemptRepository quizAttemptRepository) {

        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.documentRepository = documentRepository;
        this.quizRepository = quizRepository;
        this.flashcardRepository = flashcardRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    public DashboardResponse getDashboard(String email) {

        // 1. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 2. Count subjects
        Long totalSubjects =
                (long) subjectRepository
                        .findByUser(user)
                        .size();

        // 3. Count documents
        Long totalDocuments =
                documentRepository
                        .countBySubject_User(user);

        // 4. Count quizzes
        Long totalQuizzes =
                quizRepository
                        .countByDocument_Subject_User(user);

        // 5. Count flashcards
        Long totalFlashcards =
                flashcardRepository
                        .countByDocument_Subject_User(user);

        // 6. Get user's quiz attempts
        List<QuizAttempt> attempts =
                quizAttemptRepository.findByUser(user);

        Long totalAttempts =
                (long) attempts.size();

        // 7. Calculate average score
        Double averageScore = 0.0;

        if (!attempts.isEmpty()) {

            double totalScore = 0.0;

            for (QuizAttempt attempt : attempts) {

                totalScore += attempt.getPercentage();
            }

            averageScore =
                    totalScore / attempts.size();
        }

        // 8. Create response
        DashboardResponse response =
                new DashboardResponse();

        response.setTotalSubjects(totalSubjects);
        response.setTotalDocuments(totalDocuments);
        response.setTotalQuizzes(totalQuizzes);
        response.setTotalFlashcards(totalFlashcards);
        response.setTotalAttempts(totalAttempts);
        response.setAverageScore(averageScore);

        return response;
    }
}