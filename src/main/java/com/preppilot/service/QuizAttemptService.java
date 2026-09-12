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
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    public QuizAttemptService(
            QuizAttemptRepository quizAttemptRepository,
            QuizAnswerRepository quizAnswerRepository,
            QuizQuestionRepository quizQuestionRepository,
            QuizRepository quizRepository,
            UserRepository userRepository) {

        this.quizAttemptRepository = quizAttemptRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public QuizAttemptResponse submitAttempt(
            Long quizId,
            QuizAttemptRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getDocument().getSubject().getUser().getId()
                .equals(user.getId())) {
            throw new RuntimeException("You are not authorized to attempt this quiz");
        }

        List<QuizQuestion> questions =
                quizQuestionRepository.findByQuiz(quiz);

        if (request == null ||
                request.getAnswers() == null ||
                request.getAnswers().isEmpty()) {

            throw new RuntimeException("Please submit at least one answer");
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .user(user)
                .score(0)
                .totalQuestions(questions.size())
                .percentage(0.0)
                .attemptedAt(LocalDateTime.now())
                .build();

        attempt = quizAttemptRepository.save(attempt);

        int score = 0;
        List<QuizAnswer> answers = new ArrayList<>();

        for (QuizQuestion question : questions) {

            QuizAnswerRequest submittedAnswer = request.getAnswers()
                    .stream()
                    .filter(answer ->
                            answer.getQuestionId() != null &&
                            answer.getQuestionId()
                                    .equals(question.getId()))
                    .findFirst()
                    .orElse(null);

            if (submittedAnswer == null) {
                continue;
            }

            String selectedAnswer = submittedAnswer.getSelectedAnswer();

            if (selectedAnswer == null ||
                    selectedAnswer.isBlank()) {
                continue;
            }

            selectedAnswer = selectedAnswer.trim().toUpperCase();

            String correctAnswer =
                    question.getCorrectAnswer().trim().toUpperCase();

            boolean correct =
                    selectedAnswer.equals(correctAnswer);

            if (correct) {
                score++;
            }

            QuizAnswer quizAnswer = QuizAnswer.builder()
                    .attempt(attempt)
                    .question(question)
                    .selectedAnswer(selectedAnswer)
                    .correct(correct)
                    .build();

            answers.add(quizAnswer);
        }

        double percentage = questions.isEmpty()
                ? 0.0
                : ((double) score / questions.size()) * 100;

        attempt.setScore(score);
        attempt.setTotalQuestions(questions.size());
        attempt.setPercentage(percentage);

        quizAttemptRepository.save(attempt);
        quizAnswerRepository.saveAll(answers);

        String message = getPerformanceMessage(percentage);

        QuizAttemptResponse response = new QuizAttemptResponse();

        response.setAttemptId(attempt.getId());
        response.setQuizId(quiz.getId());
        response.setScore(attempt.getScore());
        response.setTotalQuestions(attempt.getTotalQuestions());
        response.setPercentage(attempt.getPercentage());
        response.setMessage(message);
        response.setAttemptedAt(attempt.getAttemptedAt());

        List<QuizAttemptResponse.AnswerResult> answerResults =
                answers.stream()
                        .map(answer ->
                                new QuizAttemptResponse.AnswerResult(
                                        answer.getQuestion().getId(),
                                        answer.getQuestion().getQuestion(),
                                        answer.getSelectedAnswer(),
                                        answer.getQuestion().getCorrectAnswer(),
                                        answer.getCorrect()
                                ))
                        .toList();

        response.setAnswers(answerResults);

        return response;
    }

    public List<QuizAttemptResponse> getMyAttempts(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<QuizAttempt> attempts =
                quizAttemptRepository.findByUser(user);

        List<QuizAttemptResponse> responses = new ArrayList<>();

        for (QuizAttempt attempt : attempts) {

            QuizAttemptResponse response =
                    new QuizAttemptResponse();

            response.setAttemptId(attempt.getId());
            response.setQuizId(attempt.getQuiz().getId());
            response.setScore(attempt.getScore());
            response.setTotalQuestions(
                    attempt.getTotalQuestions());
            response.setPercentage(
                    attempt.getPercentage());

            response.setMessage(
                    getPerformanceMessage(
                            attempt.getPercentage()));

            response.setAttemptedAt(
                    attempt.getAttemptedAt());

            responses.add(response);
        }

        return responses;
    }

    private String getPerformanceMessage(double percentage) {

        if (percentage >= 80) {
            return "Great job! You have a strong understanding of the topic.";
        }

        if (percentage >= 60) {
            return "Good work! Keep practicing to improve your score.";
        }

        if (percentage >= 40) {
            return "Keep practicing! Review the study material and try again.";
        }

        return "Don't give up! Review the material and try the quiz again.";
    }
}