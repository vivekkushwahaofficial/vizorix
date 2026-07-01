package com.vizorix.api.repository;

import com.vizorix.api.domain.Project;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository interface mapping persistence operations for Project entities. */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
  List<Project> findAllByUserId(UUID userId);

  Page<Project> findAllByUserId(UUID userId, Pageable pageable);
}
