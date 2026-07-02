package com.vizorix.api.controller;

import com.vizorix.api.dto.ExecutionInitResponse;
import com.vizorix.api.dto.ExecutionRequest;
import com.vizorix.api.dto.ExecutionSessionSummaryResponse;
import com.vizorix.api.dto.ExecutionStepResponse;
import com.vizorix.api.security.CurrentUserProvider;
import com.vizorix.api.service.ExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST controller coordinating HTTP requests for code execution and tracing sessions. */
@RestController
@RequestMapping("/api/execute")
@Tag(name = "Execution", description = "Endpoints for executing and tracing source code")
public class ExecutionController {

  private final ExecutionService executionService;
  private final CurrentUserProvider currentUserProvider;

  @Autowired
  public ExecutionController(
      ExecutionService executionService, CurrentUserProvider currentUserProvider) {
    this.executionService = executionService;
    this.currentUserProvider = currentUserProvider;
  }

  /**
   * Initiates code execution compilation and tracing.
   *
   * @param request compilation and execution request
   * @return standard execution init details response
   */
  @PostMapping
  @ResponseStatus(HttpStatus.ACCEPTED)
  @Operation(
      summary = "Execute code",
      description = "Compiles and starts tracing program execution steps for a project.")
  @ApiResponse(responseCode = "202", description = "Execution session successfully started")
  @ApiResponse(responseCode = "400", description = "Invalid payload properties")
  @ApiResponse(responseCode = "403", description = "Access denied for project workspace context")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<ExecutionInitResponse> execute(
      @Valid @RequestBody ExecutionRequest request) {
    UUID userId = currentUserProvider.getCurrentUserId();
    ExecutionInitResponse response = executionService.execute(request, userId);
    return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
  }

  /**
   * Gets details summary of a specific execution session.
   *
   * @param executionId target session UUID path parameter
   * @return standard execution session summary details response DTO
   */
  @GetMapping("/{id}")
  @Operation(
      summary = "Get execution session",
      description = "Fetches summary and status details of a specific execution run.")
  @ApiResponse(responseCode = "200", description = "Session summary retrieved successfully")
  @ApiResponse(responseCode = "404", description = "Execution session not found")
  public ResponseEntity<ExecutionSessionSummaryResponse> getSession(
      @PathVariable("id") UUID executionId) {
    UUID userId = currentUserProvider.getCurrentUserId();
    ExecutionSessionSummaryResponse response = executionService.getSession(executionId, userId);
    return ResponseEntity.ok(response);
  }

  /**
   * Fetches detailed data for a single step log line.
   *
   * @param executionId targeted session UUID path parameter
   * @param stepNumber step number count index
   * @return step trace logs details response DTO
   */
  @GetMapping("/{id}/step/{stepNumber}")
  @Operation(
      summary = "Get execution step",
      description = "Fetches local variables and frame stack data at a single line step.")
  @ApiResponse(responseCode = "200", description = "Step trace snapshot retrieved successfully")
  @ApiResponse(responseCode = "404", description = "Step or session details not found")
  public ResponseEntity<ExecutionStepResponse> getStep(
      @PathVariable("id") UUID executionId, @PathVariable("stepNumber") int stepNumber) {
    UUID userId = currentUserProvider.getCurrentUserId();
    ExecutionStepResponse response = executionService.getStep(executionId, stepNumber, userId);
    return ResponseEntity.ok(response);
  }

  /**
   * Cancels a running code execution trace session.
   *
   * @param executionId targeted session UUID path parameter
   * @return void payload with 204 status code
   */
  @PostMapping("/{id}/cancel")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Cancel execution",
      description = "Terminates any active subprocess run connected to this session.")
  @ApiResponse(
      responseCode = "204",
      description = "Subprocess trace request cancelled successfully")
  public ResponseEntity<Void> cancel(@PathVariable("id") UUID executionId) {
    UUID userId = currentUserProvider.getCurrentUserId();
    executionService.cancel(executionId, userId);
    return ResponseEntity.noContent().build();
  }
}
