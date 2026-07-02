package com.vizorix.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizorix.api.config.SecurityConfig;
import com.vizorix.api.dto.ExecutionInitResponse;
import com.vizorix.api.dto.ExecutionRequest;
import com.vizorix.api.dto.ExecutionSessionSummaryResponse;
import com.vizorix.api.dto.ExecutionStepResponse;
import com.vizorix.api.exception.CompilationException;
import com.vizorix.api.exception.ResourceNotFoundException;
import com.vizorix.api.security.CurrentUserProvider;
import com.vizorix.api.security.CustomUserDetailsService;
import com.vizorix.api.security.JwtAuthenticationEntryPoint;
import com.vizorix.api.security.JwtAuthenticationFilter;
import com.vizorix.api.security.JwtService;
import com.vizorix.api.service.ExecutionService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/** Unit tests for the {@link ExecutionController} utilizing MockMvc. */
@WebMvcTest(ExecutionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class})
@WithMockUser
class ExecutionControllerTests {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private ExecutionService executionService;
  @MockBean private CurrentUserProvider currentUserProvider;
  @MockBean private CustomUserDetailsService customUserDetailsService;
  @MockBean private JwtService jwtService;

  @Test
  void execute_ValidRequest_ReturnsAccepted() throws Exception {
    UUID projectId = UUID.randomUUID();
    UUID executionId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    ExecutionRequest request = new ExecutionRequest();
    request.setProjectId(projectId);
    request.setSourceCode("public class Main {}");

    ExecutionInitResponse initResponse = new ExecutionInitResponse(executionId, 5);

    when(currentUserProvider.getCurrentUserId()).thenReturn(userId);
    when(executionService.execute(any(ExecutionRequest.class), eq(userId)))
        .thenReturn(initResponse);

    mockMvc
        .perform(
            post("/api/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.executionId", Matchers.is(executionId.toString())))
        .andExpect(jsonPath("$.totalSteps", Matchers.is(5)));

    verify(executionService, times(1)).execute(any(ExecutionRequest.class), eq(userId));
  }

  @Test
  void execute_CompilationFailure_ReturnsBadRequest() throws Exception {
    UUID projectId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    ExecutionRequest request = new ExecutionRequest();
    request.setProjectId(projectId);
    request.setSourceCode("public class Main { invalid syntax }");

    when(currentUserProvider.getCurrentUserId()).thenReturn(userId);
    when(executionService.execute(any(ExecutionRequest.class), eq(userId)))
        .thenThrow(
            new CompilationException("Compilation failed", List.of("Line 2: error: syntax error")));

    mockMvc
        .perform(
            post("/api/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode", Matchers.is("COMPILATION_FAILED")))
        .andExpect(jsonPath("$.message", Matchers.containsString("Line 2: error: syntax error")));
  }

  @Test
  void getSession_ExistingSession_ReturnsSummary() throws Exception {
    UUID executionId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    ExecutionSessionSummaryResponse summary = new ExecutionSessionSummaryResponse();
    summary.setExecutionId(executionId);
    summary.setTotalSteps(12);
    summary.setStatus("SUCCESS");

    when(currentUserProvider.getCurrentUserId()).thenReturn(userId);
    when(executionService.getSession(executionId, userId)).thenReturn(summary);

    mockMvc
        .perform(get("/api/execute/" + executionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.executionId", Matchers.is(executionId.toString())))
        .andExpect(jsonPath("$.status", Matchers.is("SUCCESS")));
  }

  @Test
  void getStep_ValidStep_ReturnsStepDetails() throws Exception {
    UUID executionId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    ExecutionStepResponse stepResponse = new ExecutionStepResponse();
    stepResponse.setStep(1);
    stepResponse.setTotalSteps(5);
    stepResponse.setLineNumber(10);
    stepResponse.setClassName("Main");
    stepResponse.setMethodName("main");
    stepResponse.setStdout("Hello");
    stepResponse.setVariables(Collections.emptyList());
    stepResponse.setCallStack(Collections.emptyList());

    when(currentUserProvider.getCurrentUserId()).thenReturn(userId);
    when(executionService.getStep(executionId, 1, userId)).thenReturn(stepResponse);

    mockMvc
        .perform(get("/api/execute/" + executionId + "/step/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.step", Matchers.is(1)))
        .andExpect(jsonPath("$.lineNumber", Matchers.is(10)))
        .andExpect(jsonPath("$.stdout", Matchers.is("Hello")));
  }

  @Test
  void getStep_NonExistentSession_ReturnsNotFound() throws Exception {
    UUID executionId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    when(currentUserProvider.getCurrentUserId()).thenReturn(userId);
    when(executionService.getStep(executionId, 1, userId))
        .thenThrow(new ResourceNotFoundException("Execution session not found"));

    mockMvc
        .perform(get("/api/execute/" + executionId + "/step/1"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode", Matchers.is("RESOURCE_NOT_FOUND")));
  }

  @Test
  void cancel_RunningExecution_ReturnsNoContent() throws Exception {
    UUID executionId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    when(currentUserProvider.getCurrentUserId()).thenReturn(userId);
    doNothing().when(executionService).cancel(executionId, userId);

    mockMvc
        .perform(post("/api/execute/" + executionId + "/cancel"))
        .andExpect(status().isNoContent());

    verify(executionService, times(1)).cancel(executionId, userId);
  }
}
