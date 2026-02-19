package com.example.IssueTracker.service;

import com.example.IssueTracker.dto.ProjectRequest;
import com.example.IssueTracker.dto.ProjectResponse;
import com.example.IssueTracker.dto.UserResponse;
import com.example.IssueTracker.enums.Role;
import com.example.IssueTracker.model.Project;
import com.example.IssueTracker.model.User;
import com.example.IssueTracker.repository.ProjectRepository;
import com.example.IssueTracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public ProjectService(UserRepository userRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public ProjectResponse createProject(@RequestBody ProjectRequest projectRequest){

        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Only ADMIN or MANAGER can create projects");
        }

        Project project = new Project();
        project.setTitle(projectRequest.getTitle());
        project.setDescription(projectRequest.getDescription());
        project.setCreatedBy(currentUser);

        // Add creator as member automatically
        project.getMembers().add(currentUser);

        Project saved = projectRepository.save(project);

        return mapToResponse(saved);
    }

    public List<ProjectResponse> getAllProject() {

        User currentUser = getCurrentUser();

        List<Project> projects;

        if (currentUser.getRole() == Role.DEVELOPER) {
            projects = projectRepository.findByMembersContaining(currentUser);
        } else {
            projects = projectRepository.findAll();
        }

        return projects.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProjectResponse getProjectById(Long projectId) {

        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (currentUser.getRole() == Role.DEVELOPER &&
                !project.getMembers().contains(currentUser)) {
            throw new AccessDeniedException("Not allowed to view this project");
        }

        return mapToResponse(project);
    }

    public void deleteProject(Long projectId) {

        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Only ADMIN or MANAGER can delete projects");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectRepository.delete(project);
    }


    public ProjectResponse addMember(Long projectId, Long userId) {

        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.MANAGER) {
            throw new SecurityException("Not allowed to add members");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.getMembers().add(userToAdd);

        return mapToResponse(projectRepository.save(project));
    }

    public ProjectResponse removeMember(Long projectId, Long userId) {

        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Not allowed to remove members");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (project.getCreatedBy().getId().equals(userId)) {
            throw new IllegalStateException("Cannot remove project creator");
        }

        if (!project.getMembers().contains(userToRemove)) {
            throw new IllegalStateException("User is not a member of this project");
        }

        project.getMembers().remove(userToRemove);

        return mapToResponse(projectRepository.save(project));
    }


    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void requireAdminOrManager(User user) {
        if (user.getRole() != Role.ADMIN &&
                user.getRole() != Role.MANAGER) {
            throw new SecurityException("Only ADMIN or MANAGER allowed");
        }
    }


    private ProjectResponse mapToResponse(Project project) {
        ProjectResponse response = new ProjectResponse();

        response.setId(project.getId());
        response.setTitle(project.getTitle());
        response.setDescription(project.getDescription());
        response.setCreatedById(project.getCreatedBy().getId());

        // 🔥 ADD THIS
        response.setMembers(
                project.getMembers().stream()
                        .map(user -> {
                            UserResponse ur = new UserResponse();
                            ur.setId(user.getId());
                            ur.setName(user.getName());
                            ur.setEmail(user.getEmail());
                            ur.setRole(user.getRole());
                            return ur;
                        })
                        .toList()
        );

        return response;
    }



}