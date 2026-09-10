package com.preppilot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.preppilot.entity.QuizAttempt;
import com.preppilot.entity.Quiz;
import com.preppilot.entity.User;

public interface QuizAttemptRepository
        extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByQuiz(Quiz quiz);

    List<QuizAttempt> findByUser(User user);
}