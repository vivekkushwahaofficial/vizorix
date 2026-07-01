package com.vizorix.api.dto;

import com.vizorix.api.domain.enums.Language;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** Output data response model representing a project details view. */
@Getter
@Setter
public class ProjectResponse {
  private UUID id;
  private String name;
  private String description;
  private Language language;
  private String sourceCode;
  private UUID userId;
  private Instant createdAt;
  private Instant updatedAt;
  private Long version;
}
