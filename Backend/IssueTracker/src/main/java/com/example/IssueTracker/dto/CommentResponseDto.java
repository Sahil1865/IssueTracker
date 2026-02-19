package com.example.IssueTracker.dto;

import lombok.Data;

@Data
public class CommentResponseDto {

    private Long id;
    private String content;
    private Long userId;
    private Long ticketId;
    private String username;
}
