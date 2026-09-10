package com.preppilot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.preppilot.entity.Quiz;
import com.preppilot.entity.QuizQuestion;

public interface QuizQuestionRepository
        extends JpaRepository<QuizQuestion, Long> {

    List<QuizQuestion> findByQuiz(Quiz quiz);
}