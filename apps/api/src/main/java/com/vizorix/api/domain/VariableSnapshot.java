package com.vizorix.api.domain;

import com.vizorix.api.domain.enums.VariableScope;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Domain entity representing a variable state snapshot at a specific execution step. */
@Entity
@Table(name = "variable_snapshots")
@Getter
@Setter
public class VariableSnapshot extends BaseEntity {

  @NotBlank
  @Column(name = "name", nullable = false)
  private String name;

  @NotNull @Column(name = "value", nullable = false, columnDefinition = "TEXT")
  private String value;

  @NotBlank
  @Column(name = "type", nullable = false)
  private String type;

  @NotNull @Enumerated(EnumType.STRING)
  @Column(name = "scope", nullable = false)
  private VariableScope scope;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "step_id", nullable = false)
  private ExecutionStep step;
}
