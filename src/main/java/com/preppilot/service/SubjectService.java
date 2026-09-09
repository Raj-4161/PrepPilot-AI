package com.preppilot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.preppilot.dto.SubjectRequest;
import com.preppilot.dto.SubjectResponse;
import com.preppilot.entity.Subject;
import com.preppilot.entity.User;
import com.preppilot.repository.SubjectRepository;
import com.preppilot.repository.UserRepository;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public SubjectService(
            SubjectRepository subjectRepository,
            UserRepository userRepository) {

        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    public SubjectResponse createSubject(
            SubjectRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subject subject = new Subject();
        subject.setName(request.getName());
        subject.setDescription(request.getDescription());
        subject.setUser(user);

        Subject savedSubject = subjectRepository.save(subject);

        return convertToResponse(savedSubject);
    }

    public List<SubjectResponse> getMySubjects(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Subject> subjects = subjectRepository.findByUser(user);

        return subjects.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public SubjectResponse updateSubject(
            Long subjectId,
            SubjectRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to modify this subject");
        }

        subject.setName(request.getName());
        subject.setDescription(request.getDescription());

        Subject updatedSubject = subjectRepository.save(subject);

        return convertToResponse(updatedSubject);
    }

    public void deleteSubject(
            Long subjectId,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this subject");
        }

        subjectRepository.delete(subject);
    }

    private SubjectResponse convertToResponse(Subject subject) {

        SubjectResponse response = new SubjectResponse();

        response.setId(subject.getId());
        response.setName(subject.getName());
        response.setDescription(subject.getDescription());

        return response;
    }
}