package com.preppilot.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.preppilot.dto.QuizAttemptRequest;
import com.preppilot.dto.QuizAttemptResponse;
import com.preppilot.dto.QuizResponse;
import com.preppilot.service.QuizAttemptService;
import com.preppilot.service.QuizService;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final QuizAttemptService quizAttemptService;

    public QuizController(
            QuizService quizService,
            QuizAttemptService quizAttemptService) {

        this.quizService = quizService;
        this.quizAttemptService = quizAttemptService;
    }

    // GENERATE QUIZ
    @PostMapping("/documents/{documentId}/generate")
    public QuizResponse generateQuiz(
            @PathVariable Long documentId,
            Authentication authentication) {

        return quizService.generateQuiz(
                documentId,
                authentication.getName()
        );
    }

    // GET ALL QUIZZES OF A DOCUMENT
    @GetMapping("/document/{documentId}")
    public List<QuizResponse> getQuizzesByDocument(
            @PathVariable Long documentId,
            Authentication authentication) {

        return quizService.getQuizzesByDocument(
                documentId,
                authentication.getName()
        );
    }

    // GET SINGLE QUIZ
    @GetMapping("/{quizId}")
    public QuizResponse getQuiz(
            @PathVariable Long quizId,
            Authentication authentication) {

        return quizService.getQuiz(
                quizId,
                authentication.getName()
        );
    }

    // SUBMIT QUIZ ATTEMPT
    @PostMapping("/{quizId}/attempt")
    public QuizAttemptResponse submitAttempt(
            @PathVariable Long quizId,
            @RequestBody QuizAttemptRequest request,
            Authentication authentication) {

        return quizAttemptService.submitAttempt(
                quizId,
                request,
                authentication.getName()
        );
    }
    
 // GET MY QUIZ ATTEMPT HISTORY
    @GetMapping("/attempts/my")
    public List<QuizAttemptResponse> getMyAttempts(
            Authentication authentication) {

        return quizAttemptService.getMyAttempts(
                authentication.getName()
        );
    }
    
}