package com.example.IssueTracker.dto;


import com.example.IssueTracker.enums.Role;
import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String email;
    private String password;
}
