package com.vizorix.api.repository;

import com.vizorix.api.domain.VariableSnapshot;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository interface mapping persistence operations for VariableSnapshot entities. */
@Repository
public interface VariableSnapshotRepository extends JpaRepository<VariableSnapshot, UUID> {
  List<VariableSnapshot> findAllByStepId(UUID stepId);
}
