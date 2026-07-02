package com.vizorix.api.service;

import com.vizorix.api.domain.ExecutionSession;
import com.vizorix.api.domain.Project;
import com.vizorix.api.engine.ExecutionResult;
import com.vizorix.api.mapper.ExecutionMapper;
import com.vizorix.api.repository.ExecutionSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service managing persistence operations for code execution results. Isolated from compilation and
 * tracing details.
 */
@Service
@Transactional
public class ExecutionPersistenceService {

  private final ExecutionSessionRepository sessionRepository;
  private final ExecutionMapper mapper;

  @Autowired
  public ExecutionPersistenceService(
      ExecutionSessionRepository sessionRepository, ExecutionMapper mapper) {
    this.sessionRepository = sessionRepository;
    this.mapper = mapper;
  }

  /**
   * Persists an ExecutionResult to database entities linked to a Project.
   *
   * @param result the engine execution tracing result to persist
   * @param project the project this execution was run for
   * @return the saved ExecutionSession entity
   */
  public ExecutionSession persistResult(ExecutionResult result, Project project) {
    ExecutionSession session = mapper.toSessionEntity(result, project);
    return sessionRepository.save(session);
  }
}
