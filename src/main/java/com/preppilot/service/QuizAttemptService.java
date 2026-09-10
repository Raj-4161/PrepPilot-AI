package com.preppilot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.preppilot.dto.QuizAnswerRequest;
import com.preppilot.dto.QuizAttemptRequest;
import com.preppilot.dto.QuizAttemptResponse;
import com.preppilot.entity.Quiz;
import com.preppilot.entity.QuizAnswer;
import com.preppilot.entity.QuizAttempt;
import com.preppilot.entity.QuizQuestion;
import com.preppilot.entity.User;
import com.preppilot.repository.QuizAnswerRepository;
import com.preppilot.repository.QuizAttemptRepository;
import com.preppilot.repository.QuizQuestionRepository;
import com.preppilot.repository.QuizRepository;
import com.preppilot.repository.UserRepository;

@Service
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final UserRepository userRepository;

    public QuizAttemptService(
            QuizAttemptRepository quizAttemptRepository,
            QuizAnswerRepository quizAnswerRepository,
            QuizRepository quizRepository,
            QuizQuestionRepository quizQuestionRepository,
            UserRepository userRepository) {

        this.quizAttemptRepository = quizAttemptRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public QuizAttemptResponse submitAttempt(
            Long quizId,
            QuizAttemptRequest request,
            String email) {

        // 1. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 2. Find quiz
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() ->
                        new RuntimeException("Quiz not found"));

        // 3. Check ownership
        if (!quiz.getDocument()
                .getSubject()
                .getUser()
                .getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to attempt this quiz");
        }

        // 4. Get all questions of this quiz
        List<QuizQuestion> questions =
                quizQuestionRepository.findByQuiz(quiz);

        if (questions.isEmpty()) {
            throw new RuntimeException(
                    "Quiz has no questions");
        }

        // 5. Validate submitted answers
        if (request.getAnswers() == null ||
                request.getAnswers().isEmpty()) {

            throw new RuntimeException(
                    "Answers cannot be empty");
        }

        // 6. Create attempt
        QuizAttempt attempt = new QuizAttempt();

        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setTotalQuestions(questions.size());
        attempt.setAttemptedAt(LocalDateTime.now());

        // 7. Calculate score
        int score = 0;

        List<QuizAnswer> answersToSave =
                new ArrayList<>();

        for (QuizQuestion question : questions) {

            QuizAnswerRequest submittedAnswer =
                    request.getAnswers()
                            .stream()
                            .filter(answer ->
                                    question.getId()
                                            .equals(answer.getQuestionId()))
                            .findFirst()
                            .orElse(null);

            if (submittedAnswer == null) {
                continue;
            }

            String selectedAnswer =
                    submittedAnswer.getSelectedAnswer();

            boolean isCorrect =
                    question.getCorrectAnswer()
                            .equalsIgnoreCase(selectedAnswer);

            if (isCorrect) {
                score++;
            }

            QuizAnswer quizAnswer =
                    new QuizAnswer();

            quizAnswer.setAttempt(attempt);
            quizAnswer.setQuestion(question);
            quizAnswer.setSelectedAnswer(selectedAnswer);
            quizAnswer.setCorrect(isCorrect);

            answersToSave.add(quizAnswer);
        }

        // 8. Calculate percentage
        double percentage =
                (score * 100.0) / questions.size();

        attempt.setScore(score);
        attempt.setPercentage(percentage);

        // 9. Save attempt
        QuizAttempt savedAttempt =
                quizAttemptRepository.save(attempt);

        // 10. Save individual answers
        for (QuizAnswer answer : answersToSave) {
            quizAnswerRepository.save(answer);
        }

        // 11. Prepare response
        QuizAttemptResponse response =
                new QuizAttemptResponse();

        response.setAttemptId(
                savedAttempt.getId());

        response.setQuizId(
                quiz.getId());

        response.setScore(score);

        response.setTotalQuestions(
                questions.size());

        response.setPercentage(percentage);

        response.setMessage(
                getResultMessage(percentage));

        return response;
    }
    
    public List<QuizAttemptResponse> getMyAttempts(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<QuizAttempt> attempts =
                quizAttemptRepository.findByUser(user);

        List<QuizAttemptResponse> responses =
                new ArrayList<>();

        for (QuizAttempt attempt : attempts) {

            QuizAttemptResponse response =
                    new QuizAttemptResponse();

            response.setAttemptId(
                    attempt.getId());

            response.setQuizId(
                    attempt.getQuiz().getId());

            response.setScore(
                    attempt.getScore());

            response.setTotalQuestions(
                    attempt.getTotalQuestions());

            response.setPercentage(
                    attempt.getPercentage());

            response.setMessage(
                    getResultMessage(
                            attempt.getPercentage()));

            responses.add(response);
        }

        return responses;
    }

    private String getResultMessage(
            double percentage) {

        if (percentage >= 80) {
            return "Great job!";
        }

        if (percentage >= 60) {
            return "Good work! Keep practicing.";
        }

        if (percentage >= 40) {
            return "Keep practicing to improve.";
        }

        return "Don't give up. Review the material and try again.";
    }
}