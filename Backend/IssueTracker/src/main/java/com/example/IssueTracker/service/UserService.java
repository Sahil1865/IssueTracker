package com.example.IssueTracker.service;

import com.example.IssueTracker.dto.UserRequest;
import com.example.IssueTracker.dto.UserResponse;
import com.example.IssueTracker.enums.Role;
import com.example.IssueTracker.model.User;
import com.example.IssueTracker.repository.UserRepository;
import com.example.IssueTracker.security.JwtUtil;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public UserService(UserRepository userRepository, PasswordEncoder encoder, JwtUtil jwtUtil, AuthenticationManager authManager) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
    }

    public UserResponse signup(UserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(Role.DEVELOPER);

        User saved = userRepository.save(user);

        return mapToResponse(saved);
    }

    public String login(UserRequest request) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User dbUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));


        return jwtUtil.generateToken(
                dbUser.getEmail(),
                dbUser.getRole().name()
        );
    }

    public List<UserResponse> getAllUsers(){
        List<User> users= userRepository.findAll();

        return users.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return response;
    }
}
