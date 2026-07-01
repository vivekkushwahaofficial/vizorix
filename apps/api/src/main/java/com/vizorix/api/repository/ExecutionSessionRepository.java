package com.vizorix.api.repository;

import com.vizorix.api.domain.ExecutionSession;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository interface mapping persistence operations for ExecutionSession entities. */
@Repository
public interface ExecutionSessionRepository extends JpaRepository<ExecutionSession, UUID> {
  List<ExecutionSession> findAllByProjectId(UUID projectId);
}
