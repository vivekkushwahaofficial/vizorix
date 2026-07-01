package com.vizorix.api.controller;

import com.vizorix.api.dto.ProjectRequest;
import com.vizorix.api.dto.ProjectResponse;
import com.vizorix.api.security.CurrentUserProvider;
import com.vizorix.api.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST controller coordinating HTTP requests for managing projects. */
@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Endpoints for managing code projects workspaces")
public class ProjectController {

  private final ProjectService projectService;
  private final CurrentUserProvider currentUserProvider;

  @Autowired
  public ProjectController(ProjectService projectService, CurrentUserProvider currentUserProvider) {
    this.projectService = projectService;
    this.currentUserProvider = currentUserProvider;
  }

  /**
   * Creates a new project in the workspace.
   *
   * @param request project creation details
   * @return standard project details response DTO
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create a project",
      description =
          "Initializes a new code workspace project for the active user identity context.")
  @ApiResponse(responseCode = "201", description = "Project created successfully")
  @ApiResponse(responseCode = "400", description = "Invalid payload properties or missing headers")
  @ApiResponse(responseCode = "404", description = "User context record not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
    UUID userId = currentUserProvider.getCurrentUserId();
    ProjectResponse response = projectService.createProject(request, userId);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /**
   * Retrieves a paginated list of projects.
   *
   * @param pageable pagination parameters
   * @return paginated search results list
   */
  @GetMapping
  @Operation(
      summary = "List projects",
      description = "Retrieves a paginated list of code workspace projects for the active user.")
  @ApiResponse(responseCode = "200", description = "Projects list retrieved successfully")
  @ApiResponse(responseCode = "400", description = "Invalid pagination criteria or headers mapping")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<Page<ProjectResponse>> listProjects(@ParameterObject Pageable pageable) {
    UUID userId = currentUserProvider.getCurrentUserId();
    Page<ProjectResponse> response = projectService.listProjects(userId, pageable);
    return ResponseEntity.ok(response);
  }

  /**
   * Gets a specific project by ID.
   *
   * @param projectId target project UUID path parameter
   * @return standard project details response DTO
   */
  @GetMapping("/{id}")
  @Operation(
      summary = "Get project",
      description = "Fetches details of a specific project workspace.")
  @ApiResponse(responseCode = "200", description = "Project details retrieved successfully")
  @ApiResponse(responseCode = "400", description = "Malformed project ID format or headers")
  @ApiResponse(responseCode = "403", description = "Access denied for cross-user resource attempts")
  @ApiResponse(responseCode = "404", description = "Project resource not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<ProjectResponse> getProject(@PathVariable("id") UUID projectId) {
    UUID userId = currentUserProvider.getCurrentUserId();
    ProjectResponse response = projectService.getProject(projectId, userId);
    return ResponseEntity.ok(response);
  }

  /**
   * Updates an existing project.
   *
   * @param projectId target project UUID path parameter
   * @param request modified properties payload
   * @return updated project details response DTO
   */
  @PutMapping("/{id}")
  @Operation(
      summary = "Update project",
      description = "Replaces property fields of an existing project workspace.")
  @ApiResponse(responseCode = "200", description = "Project updated successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request fields or missing parameters")
  @ApiResponse(responseCode = "403", description = "Access denied for modifying this resource")
  @ApiResponse(responseCode = "404", description = "Project resource not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<ProjectResponse> updateProject(
      @PathVariable("id") UUID projectId, @Valid @RequestBody ProjectRequest request) {
    UUID userId = currentUserProvider.getCurrentUserId();
    ProjectResponse response = projectService.updateProject(projectId, request, userId);
    return ResponseEntity.ok(response);
  }

  /**
   * Deletes a project.
   *
   * @param projectId target project UUID to remove
   * @return void payload with 204 status code
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Delete project",
      description = "Permanently deletes a project workspace and cascades to child runs records.")
  @ApiResponse(
      responseCode = "204",
      description = "Project deleted successfully with no response body returned")
  @ApiResponse(responseCode = "400", description = "Malformed project ID or missing headers")
  @ApiResponse(responseCode = "403", description = "Access denied for deleting this resource")
  @ApiResponse(responseCode = "404", description = "Project resource not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<Void> deleteProject(@PathVariable("id") UUID projectId) {
    UUID userId = currentUserProvider.getCurrentUserId();
    projectService.deleteProject(projectId, userId);
    return ResponseEntity.noContent().build();
  }
}
