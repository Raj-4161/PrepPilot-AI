package com.preppilot.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.preppilot.entity.User;
import com.preppilot.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
}