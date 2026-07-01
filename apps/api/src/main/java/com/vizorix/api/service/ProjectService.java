package com.vizorix.api.service;

import com.vizorix.api.domain.Project;
import com.vizorix.api.domain.User;
import com.vizorix.api.dto.ProjectRequest;
import com.vizorix.api.dto.ProjectResponse;
import com.vizorix.api.exception.AccessDeniedException;
import com.vizorix.api.exception.ResourceNotFoundException;
import com.vizorix.api.mapper.ProjectMapper;
import com.vizorix.api.repository.ProjectRepository;
import com.vizorix.api.repository.UserRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class orchestrating project workspace management operations. */
@Service
@Transactional
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final ProjectMapper projectMapper;

  @Autowired
  public ProjectService(
      ProjectRepository projectRepository,
      UserRepository userRepository,
      ProjectMapper projectMapper) {
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
    this.projectMapper = projectMapper;
  }

  /**
   * Creates a new project in the system.
   *
   * @param request project properties request payload
   * @param userId the user ID context who will own this project
   * @return standard project details response DTO
   */
  public ProjectResponse createProject(ProjectRequest request, UUID userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    Project project = projectMapper.toEntity(request, user);
    Project savedProject = projectRepository.save(project);
    return projectMapper.toResponse(savedProject);
  }

  /**
   * Retrieves a paginated list of projects owned by a user.
   *
   * @param userId user account identifier
   * @param pageable pagination options mapping parameters
   * @return paginated search results mapping response DTOs
   */
  @Transactional(readOnly = true)
  public Page<ProjectResponse> listProjects(UUID userId, Pageable pageable) {
    return projectRepository.findAllByUserId(userId, pageable).map(projectMapper::toResponse);
  }

  /**
   * Fetches detailed information for a single project.
   *
   * @param projectId target project ID
   * @param userId active user context identifier
   * @return standard project details response DTO
   */
  @Transactional(readOnly = true)
  public ProjectResponse getProject(UUID projectId, UUID userId) {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Project not found with id: " + projectId));

    validateProjectOwnership(project, userId);
    return projectMapper.toResponse(project);
  }

  /**
   * Updates property fields of a project.
   *
   * @param projectId target project ID
   * @param request modified field properties request payload
   * @param userId active user context identifier
   * @return updated project details response DTO
   */
  public ProjectResponse updateProject(UUID projectId, ProjectRequest request, UUID userId) {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Project not found with id: " + projectId));

    validateProjectOwnership(project, userId);
    projectMapper.updateEntity(request, project);
    Project updatedProject = projectRepository.save(project);
    return projectMapper.toResponse(updatedProject);
  }

  /**
   * Deletes a project from the system.
   *
   * @param projectId target project ID to remove
   * @param userId active user context identifier
   */
  public void deleteProject(UUID projectId, UUID userId) {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Project not found with id: " + projectId));

    validateProjectOwnership(project, userId);
    projectRepository.delete(project);
  }

  private void validateProjectOwnership(Project project, UUID userId) {
    if (project.getUser() == null || !project.getUser().getId().equals(userId)) {
      throw new AccessDeniedException(
          "User do not have permission to access or modify this project resource");
    }
  }
}
