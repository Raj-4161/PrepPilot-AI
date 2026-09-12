package com.preppilot.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.preppilot.dto.AdminDashboardResponse;
import com.preppilot.dto.AdminUserResponse;
import com.preppilot.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {
        return adminService.getDashboard();
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/users/{userId}")
    public AdminUserResponse getUserById(
            @PathVariable Long userId) {

        return adminService.getUserById(userId);
    }

    @PutMapping("/users/{userId}/role")
    public AdminUserResponse updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role) {

        return adminService.updateUserRole(
                userId,
                role
        );
    }

    @DeleteMapping("/users/{userId}")
    public String deleteUser(
            @PathVariable Long userId,
            Authentication authentication) {

        adminService.deleteUser(
                userId,
                authentication.getName()
        );

        return "User deleted successfully";
    }
}