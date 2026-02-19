package com.example.IssueTracker.repository;

import com.example.IssueTracker.model.Project;
import com.example.IssueTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {

    List<Project> findByMembersContaining(User currentUser);
}
