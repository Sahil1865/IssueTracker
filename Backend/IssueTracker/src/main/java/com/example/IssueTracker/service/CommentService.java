package com.example.IssueTracker.service;

import com.example.IssueTracker.dto.CommentResponseDto;
import com.example.IssueTracker.model.Comment;
import com.example.IssueTracker.dto.CommentCreateDto;
import com.example.IssueTracker.model.Ticket;
import com.example.IssueTracker.model.User;
import com.example.IssueTracker.repository.CommentRepository;
import com.example.IssueTracker.repository.TicketRepository;
import com.example.IssueTracker.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public CommentService(
            CommentRepository commentRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    /* ---------------- CREATE COMMENT ---------------- */

    public CommentResponseDto addComment(CommentCreateDto request) {

        // 1. Get logged-in user
        User currentUser = getCurrentUser();

        // 2. Fetch ticket
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // 3. Create comment
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setTicket(ticket);
        comment.setFromUser(currentUser);

        Comment saved = commentRepository.save(comment);

        // 4. Map to response
        return mapToResponse(saved);
    }

    /* ---------------- GET COMMENTS BY TICKET ---------------- */

    public List<CommentResponseDto> getCommentsByTicket(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        return commentRepository.findByTicket(ticket)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* ---------------- HELPER: CURRENT USER ---------------- */

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /* ---------------- MAPPER ---------------- */

    private CommentResponseDto mapToResponse(Comment comment) {

        CommentResponseDto response = new CommentResponseDto();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setUserId(comment.getFromUser().getId());
        response.setTicketId(comment.getTicket().getId());
        response.setUsername(comment.getFromUser().getName());

        return response;
    }
}

