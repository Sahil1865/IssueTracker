package com.example.IssueTracker.controller;

import com.example.IssueTracker.dto.UserRequest;
import com.example.IssueTracker.dto.UserResponse;
import com.example.IssueTracker.model.User;
import com.example.IssueTracker.repository.UserRepository;
import com.example.IssueTracker.security.JwtUtil;
import com.example.IssueTracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final UserService userService;

    public AuthController(UserRepository repo,
                          PasswordEncoder encoder,
                          JwtUtil jwtUtil,
                          AuthenticationManager authManager, UserService userService) {
        this.userRepository = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
        this.userService = userService;
    }

    @PostMapping("/api/auth/register")
    public String signup(@RequestBody UserRequest request) {

        return  userService.signup(request)+ "User registered successfully";
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody UserRequest request) {
        String token = String.valueOf(userService.login(request));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/api/users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
}