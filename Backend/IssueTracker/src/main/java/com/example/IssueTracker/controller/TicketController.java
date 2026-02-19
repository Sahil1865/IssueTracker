package com.example.IssueTracker.controller;

import com.example.IssueTracker.dto.TicketRequest;
import com.example.IssueTracker.dto.TicketResponse;
import com.example.IssueTracker.dto.TicketStatusRequest;
import com.example.IssueTracker.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public TicketResponse createTicket(@RequestBody TicketRequest ticketRequest){
        return ticketService.createTicket(ticketRequest);
    }

    @GetMapping("/{id}")
    public TicketResponse getTicketById(@PathVariable Long id){
        return ticketService.getTicketById(id);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{id}/assign")
    public TicketResponse assignTicket(
            @PathVariable Long id,
            @RequestParam Long assigneeId
    ) {
        return ticketService.assignTicket(id, assigneeId);
    }

    @PutMapping("/{id}/status")
    public TicketResponse updateStatus(
            @PathVariable Long id,
            @RequestBody TicketStatusRequest request
    ) {
        return ticketService.moveTicket(id, request.getNewStatus());
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ticketService.getStats();
    }

    @GetMapping("/project/{projectId}")
    public List<TicketResponse> getTicketsByProject(
            @PathVariable Long projectId) {
        return ticketService.getTicketsByProject(projectId);
    }


}
