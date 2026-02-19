package com.example.IssueTracker.dto;

import com.example.IssueTracker.enums.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
