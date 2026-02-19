package com.example.IssueTracker.service;

import com.example.IssueTracker.dto.TicketRequest;
import com.example.IssueTracker.dto.TicketResponse;
import com.example.IssueTracker.dto.TicketStatusRequest;
import com.example.IssueTracker.enums.Role;
import com.example.IssueTracker.enums.Status;
import com.example.IssueTracker.model.Project;
import com.example.IssueTracker.model.Ticket;
import com.example.IssueTracker.model.User;
import com.example.IssueTracker.repository.ProjectRepository;
import com.example.IssueTracker.repository.TicketRepository;
import com.example.IssueTracker.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository,
                         ProjectRepository projectRepository,
                         UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public TicketResponse createTicket(TicketRequest request) {

        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Developers can only create in projects they belong to
        if (currentUser.getRole() == Role.DEVELOPER &&
                !project.getMembers().contains(currentUser)) {
            throw new AccessDeniedException("Not a member of this project");
        }

        User assignee = null;

        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Assignee must be member of project
            if (!project.getMembers().contains(assignee)) {
                throw new IllegalStateException("Assignee is not a project member");
            }
        }

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(Status.TODO);
        ticket.setProject(project);
        ticket.setAssignee(assignee);

        return mapToResponse(ticketRepository.save(ticket));
    }

    public TicketResponse moveTicket(Long ticketId, Status newStatus) {

        User currentUser = getCurrentUser();

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Developer can only update their own ticket
        if (currentUser.getRole() == Role.DEVELOPER) {
            if (ticket.getAssignee() == null ||
                    !ticket.getAssignee().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You can only update your own ticket");
            }
        }

        if (!isValidTransition(ticket.getStatus(), newStatus)) {
            throw new IllegalStateException(
                    "Invalid status transition: " +
                            ticket.getStatus() + " → " + newStatus
            );
        }

        ticket.setStatus(newStatus);

        Ticket updated = ticketRepository.save(ticket);
        return mapToResponse(updated);
    }

    public TicketResponse getTicketById(Long ticketId) {

        User currentUser = getCurrentUser();

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (currentUser.getRole() == Role.DEVELOPER &&
                !ticket.getProject().getMembers().contains(currentUser)) {
            throw new AccessDeniedException("Not allowed to view this ticket");
        }

        return mapToResponse(ticket);
    }

    public TicketResponse assignTicket(Long ticketId, Long assigneeId) {

        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Not allowed to assign tickets");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!ticket.getProject().getMembers().contains(assignee)) {
            throw new IllegalStateException("User is not a project member");
        }

        ticket.setAssignee(assignee);

        return mapToResponse(ticketRepository.save(ticket));
    }


    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = auth.getName(); // from JWT subject

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    private boolean isValidTransition(Status current, Status next){

        if (current == Status.TODO && next== Status.IN_PROGRESS)
            return true;

        if(current == Status.IN_PROGRESS && next == Status.COMPLETED)
            return true;

        return false;
    }

    public List<TicketResponse> getTicketsByProject(Long projectId) {

        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Developer must be member
        if (currentUser.getRole() == Role.DEVELOPER &&
                !project.getMembers().contains(currentUser)) {
            throw new AccessDeniedException("Not allowed to view this project");
        }

        List<Ticket> tickets = ticketRepository.findByProjectId(projectId);

        return tickets.stream()
                .map(this::mapToResponse)
                .toList();
    }



    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setStatus(ticket.getStatus());
        response.setPriority(ticket.getPriority());
        response.setProjectId(ticket.getProject().getId());

        if (ticket.getAssignee() != null) {
            response.setAssigneeId(ticket.getAssignee().getId());
            response.setAssigneeName(ticket.getAssignee().getName());
        }

        return response;
    }


    public ResponseEntity<?> getStats() {
        User currentUser = getCurrentUser();

        long total;
        long todo;
        long inProgress;
        long completed;

        if (currentUser.getRole() == Role.DEVELOPER) {

            List<Project> projects =
                    projectRepository.findByMembersContaining(currentUser);

            List<Long> projectIds =
                    projects.stream().map(Project::getId).toList();

            total = ticketRepository.countByProjectIdIn(projectIds);
            todo = ticketRepository.countByProjectIdInAndStatus(projectIds, Status.TODO);
            inProgress = ticketRepository.countByProjectIdInAndStatus(projectIds, Status.IN_PROGRESS);
            completed = ticketRepository.countByProjectIdInAndStatus(projectIds, Status.COMPLETED);

        } else {
            total = ticketRepository.count();
            todo = ticketRepository.countByStatus(Status.TODO);
            inProgress = ticketRepository.countByStatus(Status.IN_PROGRESS);
            completed = ticketRepository.countByStatus(Status.COMPLETED);
        }

        return ResponseEntity.ok(
                Map.of(
                        "total", total,
                        "open", todo,
                        "inProgress", inProgress,
                        "done", completed
                )
        );
    }
}
