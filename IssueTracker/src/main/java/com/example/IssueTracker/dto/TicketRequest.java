package com.example.IssueTracker.dto;

import com.example.IssueTracker.enums.Priority;
import com.example.IssueTracker.model.Project;
import com.example.IssueTracker.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketRequest {
    private String title;
    private String description;
    private Priority priority;
    private Long assigneeId;
    private Long projectId;
}
