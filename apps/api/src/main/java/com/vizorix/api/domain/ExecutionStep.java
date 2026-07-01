package com.vizorix.api.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Domain entity representing a single execution step line log frame in a debug session. */
@Entity
@Table(name = "execution_steps")
@Getter
@Setter
public class ExecutionStep extends BaseEntity {

  @Min(1)
  @Column(name = "step_number", nullable = false)
  private int stepNumber;

  @Min(1)
  @Column(name = "line_number", nullable = false)
  private int lineNumber;

  @NotBlank
  @Column(name = "method_name", nullable = false)
  private String methodName;

  @NotBlank
  @Column(name = "class_name", nullable = false)
  private String className;

  @Column(name = "stdout", columnDefinition = "TEXT")
  private String stdout;

  @Column(name = "stderr", columnDefinition = "TEXT")
  private String stderr;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", nullable = false)
  private ExecutionSession session;

  @OneToMany(
      mappedBy = "step",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<VariableSnapshot> snapshots = new ArrayList<>();
}
