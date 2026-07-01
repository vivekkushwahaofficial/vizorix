package com.vizorix.api.mapper;

import com.vizorix.api.domain.Project;
import com.vizorix.api.domain.User;
import com.vizorix.api.dto.ProjectRequest;
import com.vizorix.api.dto.ProjectResponse;
import org.springframework.stereotype.Component;

/** Spring Component responsible for mapping between project entity models and DTO structures. */
@Component
public class ProjectMapper {

  /**
   * Maps a ProjectRequest to a new Project entity instance linked to the provided User owner.
   *
   * @param request input payload containing properties
   * @param user user account owner mapping context
   * @return initialized Project entity model
   */
  public Project toEntity(ProjectRequest request, User user) {
    if (request == null) {
      return null;
    }
    Project project = new Project();
    project.setName(request.getName());
    project.setDescription(request.getDescription());
    project.setLanguage(request.getLanguage());
    project.setSourceCode(request.getSourceCode());
    project.setUser(user);
    return project;
  }

  /**
   * Maps a Project entity model instance to a ProjectResponse DTO view representation.
   *
   * @param project the source Project entity model
   * @return response details view model
   */
  public ProjectResponse toResponse(Project project) {
    if (project == null) {
      return null;
    }
    ProjectResponse response = new ProjectResponse();
    response.setId(project.getId());
    response.setName(project.getName());
    response.setDescription(project.getDescription());
    response.setLanguage(project.getLanguage());
    response.setSourceCode(project.getSourceCode());
    if (project.getUser() != null) {
      response.setUserId(project.getUser().getId());
    }
    response.setCreatedAt(project.getCreatedAt());
    response.setUpdatedAt(project.getUpdatedAt());
    response.setVersion(project.getVersion());
    return response;
  }

  /**
   * Updates an existing Project entity instance with fields from a ProjectRequest.
   *
   * @param request the input payload containing modified properties
   * @param project the target Project entity to mutate
   */
  public void updateEntity(ProjectRequest request, Project project) {
    if (request == null || project == null) {
      return;
    }
    project.setName(request.getName());
    project.setDescription(request.getDescription());
    project.setLanguage(request.getLanguage());
    project.setSourceCode(request.getSourceCode());
  }
}
