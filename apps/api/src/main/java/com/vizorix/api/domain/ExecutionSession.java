package com.vizorix.api.domain;

import com.vizorix.api.domain.enums.SessionStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Domain entity representing an execution or debugging runtime session. */
@Entity
@Table(name = "execution_sessions")
@Getter
@Setter
public class ExecutionSession extends BaseEntity {

  @NotNull @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private SessionStatus status;

  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;

  @Column(name = "compile_time_ms")
  private Long compileTimeMs;

  @Column(name = "execution_time_ms")
  private Long executionTimeMs;

  @Column(name = "total_steps")
  private Integer totalSteps;

  @Column(name = "total_variables")
  private Integer totalVariables;

  @Column(name = "total_console_lines")
  private Integer totalConsoleLines;

  @Column(name = "max_stack_depth")
  private Integer maxStackDepth;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @OneToMany(
      mappedBy = "session",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<ExecutionStep> steps = new ArrayList<>();
}
