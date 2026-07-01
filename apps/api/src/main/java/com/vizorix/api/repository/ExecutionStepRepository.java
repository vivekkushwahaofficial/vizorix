package com.vizorix.api.repository;

import com.vizorix.api.domain.ExecutionStep;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository interface mapping persistence operations for ExecutionStep entities. */
@Repository
public interface ExecutionStepRepository extends JpaRepository<ExecutionStep, UUID> {
  List<ExecutionStep> findAllBySessionIdOrderByStepNumberAsc(UUID sessionId);
}
