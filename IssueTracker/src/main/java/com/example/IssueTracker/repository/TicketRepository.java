package com.example.IssueTracker.repository;

import com.example.IssueTracker.enums.Status;
import com.example.IssueTracker.model.Ticket;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    long countByStatus(Status status);
    List<Ticket> findByProjectId(Long projectId);
    long countByProjectIdIn(List<Long> projectIds);
    long countByProjectIdInAndStatus(List<Long> projectIds, Status status);
}
