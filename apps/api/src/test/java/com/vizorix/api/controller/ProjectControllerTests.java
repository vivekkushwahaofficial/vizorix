package com.vizorix.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizorix.api.domain.enums.Language;
import com.vizorix.api.dto.ProjectRequest;
import com.vizorix.api.dto.ProjectResponse;
import com.vizorix.api.exception.AccessDeniedException;
import com.vizorix.api.exception.ResourceNotFoundException;
import com.vizorix.api.security.CurrentUserProvider;
import com.vizorix.api.service.ProjectService;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Controller tests for Project REST operations using MockMvc. */
@WebMvcTest(ProjectController.class)
class ProjectControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private ProjectService projectService;

  @MockBean private CurrentUserProvider currentUserProvider;

  private final UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private final UUID projectId = UUID.fromString("22222222-2222-2222-2222-222222222222");

  /** Tests that a valid ProjectRequest successfully returns HTTP 201 with project details. */
  @Test
  void testCreateProjectSuccess() throws Exception {
    ProjectRequest request = new ProjectRequest();
    request.setName("Java Web Parser");
    request.setDescription("AI parsing workspace demo");
    request.setLanguage(Language.JAVA);
    request.setSourceCode("public class Main {}");

    ProjectResponse response = new ProjectResponse();
    response.setId(projectId);
    response.setName("Java Web Parser");
    response.setDescription("AI parsing workspace demo");
    response.setLanguage(Language.JAVA);
    response.setSourceCode("public class Main {}");
    response.setUserId(userId);
    response.setCreatedAt(Instant.now());
    response.setUpdatedAt(Instant.now());
    response.setVersion(0L);

    when(currentUserProvider.getCurrentUserId()).thenReturn(userId);
    when(projectService.createProject(any(ProjectRequest.class), eq(userId))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/projects")
                .header("X-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(projectId.toString()))
        .andExpect(jsonPath("$.name").value("Java Web Parser"))
        .andExpect(jsonPath("$.userId").value(userId.toString()));
  }

  /** Tests that field constraint failures return HTTP 400 Bad Request with details. */
  @Test
  void testCreateProjectValidationFailure() throws Exception {
    ProjectRequest request = new ProjectRequest();
    request.setName(""); // Invalid: blank
    request.setLanguage(Language.JAVA);
    request.setSourceCode(""); // Invalid: blank

    mockMvc
        .perform(
            post("/api/projects")
                .header("X-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.message").value(Matchers.containsString("name")))
        .andExpect(jsonPath("$.message").value(Matchers.containsString("sourceCode")));
  }

  /** Tests that missing header credentials return HTTP 400 Bad Request. */
  @Test
  void testCreateProjectMissingHeader() throws Exception {
    ProjectRequest request = new ProjectRequest();
    request.setName("Java Web Parser");
    request.setLanguage(Language.JAVA);
    request.setSourceCode("public class Main {}");

    when(currentUserProvider.getCurrentUserId())
        .thenThrow(new IllegalArgumentException("Required request header 'X-User-Id' is missing"));

    mockMvc
        .perform(
            post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.message").value("Required request header 'X-User-Id' is missing"));
  }

  /** Tests list project pagination endpoints. */
  @Test
  void testListProjectsPaginated() throws Exception {
    ProjectResponse response = new ProjectResponse();
    response.setId(projectId);
    response.setName("Demo Project");
    response.setLanguage(Language.JAVA);
    response.setSourceCode("class Test {}");
    response.setUserId(userId);

    when(currentUserProvider.getCurrentUserId()).thenReturn(userId);
    when(projectService.listProjects(eq(userId), any(PageRequest.class)))
        .thenReturn(new PageImpl<>(Collections.singletonList(response)));

    mockMvc
        .perform(
            get("/api/projects")
                .header("X-User-Id", userId.toString())
                .param("page", "0")
                .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(projectId.toString()))
        .andExpect(jsonPath("$.content[0].name").value("Demo Project"));
  }

  /** Tests that fetching non-existent projects returns HTTP 404. */
  @Test
  void testGetProjectNotFound() throws Exception {
    when(currentUserProvider.getCurrentUserId()).thenReturn(userId);
    when(projectService.getProject(eq(projectId), eq(userId)))
        .thenThrow(new ResourceNotFoundException("Project not found with id: " + projectId));

    mockMvc
        .perform(get("/api/projects/" + projectId).header("X-User-Id", userId.toString()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Project not found with id: " + projectId));
  }

  /** Tests that accessing another user's project returns HTTP 403. */
  @Test
  void testGetProjectAccessDenied() throws Exception {
    when(currentUserProvider.getCurrentUserId()).thenReturn(userId);
    when(projectService.getProject(eq(projectId), eq(userId)))
        .thenThrow(
            new AccessDeniedException(
                "User do not have permission to access or modify this project resource"));

    mockMvc
        .perform(get("/api/projects/" + projectId).header("X-User-Id", userId.toString()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
        .andExpect(
            jsonPath("$.message")
                .value("User do not have permission to access or modify this project resource"));
  }

  /** Tests that deleting a project successfully returns HTTP 204 No Content. */
  @Test
  void testDeleteProjectSuccess() throws Exception {
    when(currentUserProvider.getCurrentUserId()).thenReturn(userId);
    doNothing().when(projectService).deleteProject(eq(projectId), eq(userId));

    mockMvc
        .perform(delete("/api/projects/" + projectId).header("X-User-Id", userId.toString()))
        .andExpect(status().isNoContent());

    verify(projectService, times(1)).deleteProject(eq(projectId), eq(userId));
  }
}
