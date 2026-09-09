package com.preppilot.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.preppilot.dto.SubjectRequest;
import com.preppilot.dto.SubjectResponse;
import com.preppilot.service.SubjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public SubjectResponse createSubject(
            @Valid @RequestBody SubjectRequest request,
            Authentication authentication) {

        return subjectService.createSubject(
                request,
                authentication.getName()
        );
    }

    @GetMapping
    public List<SubjectResponse> getMySubjects(
            Authentication authentication) {

        return subjectService.getMySubjects(
                authentication.getName()
        );
    }

    @PutMapping("/{id}")
    public SubjectResponse updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request,
            Authentication authentication) {

        return subjectService.updateSubject(
                id,
                request,
                authentication.getName()
        );
    }

    @DeleteMapping("/{id}")
    public String deleteSubject(
            @PathVariable Long id,
            Authentication authentication) {

        subjectService.deleteSubject(
                id,
                authentication.getName()
        );

        return "Subject deleted successfully";
    }
}