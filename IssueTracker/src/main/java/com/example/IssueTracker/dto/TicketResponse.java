package com.example.IssueTracker.dto;

import com.example.IssueTracker.enums.Priority;
import com.example.IssueTracker.enums.Status;
import lombok.Data;

@Data
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private Long assigneeId;
    private String assigneeName;
    private Long projectId;
}
