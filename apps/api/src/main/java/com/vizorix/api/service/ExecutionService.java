package com.vizorix.api.service;

import com.vizorix.api.domain.ExecutionSession;
import com.vizorix.api.domain.ExecutionStep;
import com.vizorix.api.domain.Project;
import com.vizorix.api.domain.enums.SessionStatus;
import com.vizorix.api.dto.ExecutionInitResponse;
import com.vizorix.api.dto.ExecutionRequest;
import com.vizorix.api.dto.ExecutionSessionSummaryResponse;
import com.vizorix.api.dto.ExecutionStepResponse;
import com.vizorix.api.engine.CompilationResult;
import com.vizorix.api.engine.ExecutionResult;
import com.vizorix.api.engine.StackFrameData;
import com.vizorix.api.exception.AccessDeniedException;
import com.vizorix.api.exception.CompilationException;
import com.vizorix.api.exception.ExecutionEngineException;
import com.vizorix.api.exception.ResourceNotFoundException;
import com.vizorix.api.mapper.ExecutionMapper;
import com.vizorix.api.repository.ExecutionSessionRepository;
import com.vizorix.api.repository.ExecutionStepRepository;
import com.vizorix.api.repository.ProjectRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating compile, trace, and session management operations. Coordinates
 * CompilerService, ExecutionTracer, and ExecutionPersistenceService.
 */
@Service
public class ExecutionService {

  private static final Logger log = LoggerFactory.getLogger(ExecutionService.class);
  private static final int TIMEOUT_SECONDS = 30;

  private final ProjectRepository projectRepository;
  private final ExecutionSessionRepository sessionRepository;
  private final ExecutionStepRepository stepRepository;
  private final List<CompilerService> compilers;
  private final ExecutionTracer tracer;
  private final ExecutionPersistenceService persistenceService;
  private final ExecutionMapper mapper;

  /** Active tracers lookup to handle cancellation requests. */
  private final Map<UUID, ExecutionTracer> activeTracers = new ConcurrentHashMap<>();

  @Autowired
  public ExecutionService(
      ProjectRepository projectRepository,
      ExecutionSessionRepository sessionRepository,
      ExecutionStepRepository stepRepository,
      List<CompilerService> compilers,
      ExecutionTracer tracer,
      ExecutionPersistenceService persistenceService,
      ExecutionMapper mapper) {
    this.projectRepository = projectRepository;
    this.sessionRepository = sessionRepository;
    this.stepRepository = stepRepository;
    this.compilers = compilers;
    this.tracer = tracer;
    this.persistenceService = persistenceService;
    this.mapper = mapper;
  }

  /**
   * Compiles and traces Java source code, returning execution session IDs.
   *
   * @param request execution trigger details
   * @param userId user account identifier context
   * @return standard execution init payload
   */
  public ExecutionInitResponse execute(ExecutionRequest request, UUID userId) {
    Project project =
        projectRepository
            .findById(request.getProjectId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Project not found with id: " + request.getProjectId()));

    validateProjectOwnership(project, userId);

    // Get matching compiler
    CompilerService compiler =
        compilers.stream()
            .filter(c -> c.language().equalsIgnoreCase(project.getLanguage().name()))
            .findFirst()
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Unsupported language compiler context: " + project.getLanguage()));

    // Create temp directory for compilation outputs
    Path tempDir;
    try {
      tempDir = Files.createTempDirectory("vizorix-exec-");
    } catch (IOException e) {
      throw new ExecutionEngineException("Failed to initialize sandbox workspace", e);
    }

    long compileStartTime = System.currentTimeMillis();
    CompilationResult compileResult = null;
    CompilationException compileError = null;

    try {
      compileResult = compiler.compile(request.getSourceCode(), tempDir);
    } catch (CompilationException e) {
      compileError = e;
    }
    long compileTimeMs = System.currentTimeMillis() - compileStartTime;

    if (compileError != null) {
      // Create failed session directly in DB
      ExecutionSession session = new ExecutionSession();
      session.setProject(project);
      session.setStatus(SessionStatus.FAILED);
      session.setErrorMessage(
          "Compilation failed: " + String.join("\n", compileError.getDiagnostics()));
      session.setCompileTimeMs(compileTimeMs);
      session.setExecutionTimeMs(0L);
      session.setTotalSteps(0);
      session.setTotalVariables(0);
      session.setTotalConsoleLines(0);
      session.setMaxStackDepth(0);
      sessionRepository.save(session);

      cleanDirectory(tempDir);
      throw compileError;
    }

    // Trace compiled program
    ExecutionResult traceResult;
    UUID sessionId = UUID.randomUUID();
    activeTracers.put(sessionId, tracer);

    try {
      traceResult =
          tracer.trace(compileResult.outputDir(), compileResult.mainClassName(), TIMEOUT_SECONDS);
    } finally {
      activeTracers.remove(sessionId);
      cleanDirectory(tempDir);
    }

    // Set compile time metadata on result before persistence mapping
    ExecutionResult finalizedResult =
        new ExecutionResult(
            traceResult.steps(),
            compileTimeMs,
            traceResult.executionTimeMs(),
            traceResult.succeeded(),
            traceResult.errorMessage());

    ExecutionSession savedSession = persistenceService.persistResult(finalizedResult, project);

    // Set custom UUID generated above to match track
    savedSession.setId(sessionId);
    sessionRepository.saveAndFlush(savedSession);

    return new ExecutionInitResponse(savedSession.getId(), savedSession.getTotalSteps());
  }

  /**
   * Fetches detailed information for a single step log line.
   *
   * @param executionId session UUID
   * @param stepNumber targeted step 1-indexed count
   * @param userId user identifier validation context
   * @return standard step snapshot payload DTO
   */
  @Transactional(readOnly = true)
  public ExecutionStepResponse getStep(UUID executionId, int stepNumber, UUID userId) {
    ExecutionSession session =
        sessionRepository
            .findById(executionId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Execution session not found with id: " + executionId));

    validateProjectOwnership(session.getProject(), userId);

    List<ExecutionStep> steps = stepRepository.findAllBySessionIdOrderByStepNumberAsc(executionId);
    if (steps.isEmpty()) {
      throw new ResourceNotFoundException("No steps recorded in execution session: " + executionId);
    }

    if (stepNumber < 1 || stepNumber > steps.size()) {
      throw new IllegalArgumentException(
          String.format("Step number %d out of bounds (1..%d)", stepNumber, steps.size()));
    }

    ExecutionStep step = steps.get(stepNumber - 1);

    // Compute call stack dynamically for this step from methodName/className
    // M10 default: simulate Call Stack frame hierarchy from database method fields
    List<StackFrameData> callStack =
        List.of(
            new StackFrameData(step.getClassName(), step.getMethodName(), step.getLineNumber()));

    return mapper.toStepResponse(step, steps.size(), callStack);
  }

  /**
   * Retrieves summary performance details of an execution session.
   *
   * @param executionId target session UUID
   * @param userId validation check context
   * @return summary session details response DTO
   */
  @Transactional(readOnly = true)
  public ExecutionSessionSummaryResponse getSession(UUID executionId, UUID userId) {
    ExecutionSession session =
        sessionRepository
            .findById(executionId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Execution session not found with id: " + executionId));

    validateProjectOwnership(session.getProject(), userId);
    return mapper.toSummaryResponse(session);
  }

  /**
   * Requests runtime cancellation on active subprocesses execution tracers.
   *
   * @param executionId targeted session UUID
   * @param userId validation context
   */
  public void cancel(UUID executionId, UUID userId) {
    ExecutionSession session =
        sessionRepository
            .findById(executionId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Execution session not found with id: " + executionId));

    validateProjectOwnership(session.getProject(), userId);

    ExecutionTracer activeTracer = activeTracers.get(executionId);
    if (activeTracer instanceof JdiExecutionTracer jdiTracer) {
      jdiTracer.cancel();
    }

    session.setStatus(SessionStatus.FAILED);
    session.setErrorMessage("Cancelled by user");
    sessionRepository.save(session);
  }

  private void validateProjectOwnership(Project project, UUID userId) {
    if (project.getUser() == null || !project.getUser().getId().equals(userId)) {
      throw new AccessDeniedException(
          "User does not have access permission to this execution context resource");
    }
  }

  private void cleanDirectory(Path path) {
    try (var stream = Files.walk(path)) {
      stream
          .sorted(Comparator.reverseOrder())
          .forEach(
              p -> {
                try {
                  Files.delete(p);
                } catch (IOException ignored) {
                }
              });
    } catch (IOException e) {
      log.warn("Failed to cleanup temp workspace folder: {}", path, e);
    }
  }
}
