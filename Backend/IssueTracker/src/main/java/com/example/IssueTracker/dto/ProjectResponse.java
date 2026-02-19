package com.example.IssueTracker.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectResponse {
    private Long id;
    private String title;
    private String description;
    private Long createdById;
    List<UserResponse> members;
}
