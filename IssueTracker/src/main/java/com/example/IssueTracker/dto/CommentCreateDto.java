package com.example.IssueTracker.dto;

import lombok.Data;

@Data
public class CommentCreateDto {

    private String content;
    private Long ticketId;

}
