package com.example.IssueTracker.controller;

import com.example.IssueTracker.dto.ProjectRequest;
import com.example.IssueTracker.dto.ProjectResponse;
import com.example.IssueTracker.model.Project;
import com.example.IssueTracker.repository.ProjectRepository;
import com.example.IssueTracker.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/projects")
@RestController
public class ProjectController {


    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping
    public ProjectResponse createProject(@RequestBody ProjectRequest request) {
        return projectService.createProject(request);
    }

    @GetMapping
    public List<ProjectResponse> getProjects(){
        return projectService.getAllProject();
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id){
        projectService.deleteProject(id);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{projectId}/members")
    public ProjectResponse addMember(
            @PathVariable Long projectId,
            @RequestParam Long userId
    ) {
        return projectService.addMember(projectId, userId);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @DeleteMapping("/{projectId}/members/{userId}")
    public ProjectResponse removeMember(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        return projectService.removeMember(projectId, userId);
    }

}
