package com.preppilot.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/test")
    public Map<String, String> adminTest() {
        return Map.of(
                "message", "Welcome Admin",
                "role", "ADMIN"
        );
    }
}