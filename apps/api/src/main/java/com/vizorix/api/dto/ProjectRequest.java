package com.vizorix.api.dto;

import com.vizorix.api.domain.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Request payload structure for creating and updating project resources. */
@Getter
@Setter
public class ProjectRequest {

  @NotBlank
  @Size(max = 255)
  private String name;

  @Size(max = 1000)
  private String description;

  @NotNull private Language language;

  @NotBlank private String sourceCode;
}
