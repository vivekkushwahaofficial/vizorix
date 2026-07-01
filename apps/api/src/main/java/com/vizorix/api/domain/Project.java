package com.vizorix.api.domain;

import com.vizorix.api.domain.enums.Language;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Domain entity representing a user's code workspace project in Vizorix. */
@Entity
@Table(name = "projects")
@Getter
@Setter
public class Project extends BaseEntity {

  @NotBlank
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @NotNull @Enumerated(EnumType.STRING)
  @Column(name = "language", nullable = false)
  private Language language;

  @NotBlank
  @Column(name = "source_code", nullable = false, columnDefinition = "TEXT")
  private String sourceCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(
      mappedBy = "project",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<ExecutionSession> sessions = new ArrayList<>();
}
