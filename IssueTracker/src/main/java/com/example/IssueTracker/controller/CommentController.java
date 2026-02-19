package com.example.IssueTracker.controller;

import com.example.IssueTracker.dto.CommentCreateDto;
import com.example.IssueTracker.dto.CommentResponseDto;
import com.example.IssueTracker.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /* ---------------- ADD COMMENT ---------------- */

    @PostMapping
    public CommentResponseDto addComment(
            @RequestBody CommentCreateDto request
    ) {
        return commentService.addComment(request);
    }

    /* ---------------- GET COMMENTS FOR A TICKET ---------------- */

    @GetMapping("/ticket/{ticketId}")
    public List<CommentResponseDto> getCommentsByTicket(
            @PathVariable Long ticketId
    ) {
        return commentService.getCommentsByTicket(ticketId);
    }
}
