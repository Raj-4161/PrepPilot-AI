package com.preppilot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.preppilot.ai.GeminiService;
import com.preppilot.dto.QuizQuestionResponse;
import com.preppilot.dto.QuizResponse;
import com.preppilot.entity.Document;
import com.preppilot.entity.Quiz;
import com.preppilot.entity.QuizQuestion;
import com.preppilot.entity.User;
import com.preppilot.repository.DocumentRepository;
import com.preppilot.repository.QuizQuestionRepository;
import com.preppilot.repository.QuizRepository;
import com.preppilot.repository.UserRepository;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final QuizParserService quizParserService;

    public QuizService(
            QuizRepository quizRepository,
            QuizQuestionRepository quizQuestionRepository,
            DocumentRepository documentRepository,
            UserRepository userRepository,
            GeminiService geminiService,
            QuizParserService quizParserService) {

        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.geminiService = geminiService;
        this.quizParserService = quizParserService;
    }

    // =========================================================
    // FIND DOCUMENT
    // =========================================================

    public Document getDocument(Long documentId) {

        return documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new RuntimeException("Document not found"));
    }

    // =========================================================
    // CHECK DOCUMENT OWNERSHIP
    // =========================================================

    public void checkDocumentOwnership(
            Document document,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!document.getSubject()
                .getUser()
                .getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to access this document");
        }
    }

    // =========================================================
    // GET EXTRACTED TEXT
    // =========================================================

    public String getExtractedText(Document document) {

        String extractedText =
                document.getExtractedText();

        if (extractedText == null ||
                extractedText.isBlank()) {

            throw new RuntimeException(
                    "No extracted text available for this document");
        }

        return extractedText;
    }

    // =========================================================
    // GENERATE QUIZ
    // =========================================================

    public QuizResponse generateQuiz(
            Long documentId,
            String email) {

        // 1. Find document
        Document document =
                getDocument(documentId);

        // 2. Check ownership
        checkDocumentOwnership(
                document,
                email);

        // 3. Get extracted PDF text
        String extractedText =
                getExtractedText(document);

        // 4. Generate quiz using Gemini
        String quizText =
                geminiService.generateQuiz(
                        extractedText);

        // 5. Parse Gemini response
        List<QuizQuestionResponse> questions =
                quizParserService.parseQuiz(
                        quizText);

        // 6. Create Quiz entity
        Quiz quiz = new Quiz();

        quiz.setDocument(document);
        quiz.setTitle("AI Generated Quiz");
        quiz.setCreatedAt(LocalDateTime.now());

        // 7. Save Quiz
        Quiz savedQuiz =
                quizRepository.save(quiz);

        // 8. Save questions
        List<QuizQuestionResponse> savedQuestions =
                new ArrayList<>();

        for (QuizQuestionResponse questionResponse
                : questions) {

            QuizQuestion question =
                    new QuizQuestion();

            question.setQuiz(savedQuiz);

            question.setQuestion(
                    questionResponse.getQuestion());

            question.setOptionA(
                    questionResponse.getOptionA());

            question.setOptionB(
                    questionResponse.getOptionB());

            question.setOptionC(
                    questionResponse.getOptionC());

            question.setOptionD(
                    questionResponse.getOptionD());

            question.setCorrectAnswer(
                    questionResponse.getCorrectAnswer());

            QuizQuestion savedQuestion =
                    quizQuestionRepository.save(question);

            // Create response with generated DB ID
            QuizQuestionResponse savedResponse =
                    new QuizQuestionResponse();

            savedResponse.setId(
                    savedQuestion.getId());

            savedResponse.setQuestion(
                    savedQuestion.getQuestion());

            savedResponse.setOptionA(
                    savedQuestion.getOptionA());

            savedResponse.setOptionB(
                    savedQuestion.getOptionB());

            savedResponse.setOptionC(
                    savedQuestion.getOptionC());

            savedResponse.setOptionD(
                    savedQuestion.getOptionD());

            savedResponse.setCorrectAnswer(
                    savedQuestion.getCorrectAnswer());

            savedQuestions.add(savedResponse);
        }

        // 9. Create final response
        QuizResponse response =
                new QuizResponse();

        response.setId(
                savedQuiz.getId());

        response.setDocumentId(
                document.getId());

        response.setTitle(
                savedQuiz.getTitle());

        response.setCreatedAt(
                savedQuiz.getCreatedAt());

        response.setQuestions(
                savedQuestions);

        return response;
    }
    
    public List<QuizResponse> getQuizzesByDocument(
            Long documentId,
            String email) {

        Document document = getDocument(documentId);

        checkDocumentOwnership(
                document,
                email
        );

        List<Quiz> quizzes =
                quizRepository.findByDocument(document);

        List<QuizResponse> responses =
                new ArrayList<>();

        for (Quiz quiz : quizzes) {

            List<QuizQuestion> quizQuestions =
                    quizQuestionRepository.findByQuiz(quiz);

            List<QuizQuestionResponse> questionResponses =
                    new ArrayList<>();

            for (QuizQuestion question : quizQuestions) {

                QuizQuestionResponse response =
                        new QuizQuestionResponse();

                response.setId(question.getId());

                response.setQuestion(
                        question.getQuestion());

                response.setOptionA(
                        question.getOptionA());

                response.setOptionB(
                        question.getOptionB());

                response.setOptionC(
                        question.getOptionC());

                response.setOptionD(
                        question.getOptionD());

                response.setCorrectAnswer(
                        question.getCorrectAnswer());

                questionResponses.add(response);
            }

            QuizResponse quizResponse =
                    new QuizResponse();

            quizResponse.setId(
                    quiz.getId());

            quizResponse.setDocumentId(
                    document.getId());

            quizResponse.setTitle(
                    quiz.getTitle());

            quizResponse.setCreatedAt(
                    quiz.getCreatedAt());

            quizResponse.setQuestions(
                    questionResponses);

            responses.add(quizResponse);
        }

        return responses;
    }

    // =========================================================
    // GET SAVED QUIZ
    // =========================================================

    public QuizResponse getQuiz(
            Long quizId,
            String email) {

        // 1. Find logged-in user
        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        // 2. Find quiz
        Quiz quiz =
                quizRepository.findById(quizId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Quiz not found"));

        // 3. Check quiz ownership
        if (!quiz.getDocument()
                .getSubject()
                .getUser()
                .getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to access this quiz");
        }

        // 4. Get questions
        List<QuizQuestion> quizQuestions =
                quizQuestionRepository.findByQuiz(quiz);

        // 5. Convert entities to DTOs
        List<QuizQuestionResponse> questionResponses =
                new ArrayList<>();

        for (QuizQuestion question
                : quizQuestions) {

            QuizQuestionResponse response =
                    new QuizQuestionResponse();

            response.setId(
                    question.getId());

            response.setQuestion(
                    question.getQuestion());

            response.setOptionA(
                    question.getOptionA());

            response.setOptionB(
                    question.getOptionB());

            response.setOptionC(
                    question.getOptionC());

            response.setOptionD(
                    question.getOptionD());

            response.setCorrectAnswer(
                    question.getCorrectAnswer());

            questionResponses.add(response);
        }

        // 6. Create QuizResponse
        QuizResponse quizResponse =
                new QuizResponse();

        quizResponse.setId(
                quiz.getId());

        quizResponse.setDocumentId(
                quiz.getDocument().getId());

        quizResponse.setTitle(
                quiz.getTitle());

        quizResponse.setCreatedAt(
                quiz.getCreatedAt());

        quizResponse.setQuestions(
                questionResponses);

        return quizResponse;
    }
}