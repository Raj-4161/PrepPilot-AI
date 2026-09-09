package com.preppilot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.preppilot.entity.Subject;
import com.preppilot.entity.User;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByUser(User user);
}