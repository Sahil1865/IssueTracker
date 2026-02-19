package com.example.IssueTracker.dto;

import com.example.IssueTracker.enums.Status;
import lombok.Data;

@Data
public class TicketStatusRequest {
    private Long id;
    private Status newStatus;
}
