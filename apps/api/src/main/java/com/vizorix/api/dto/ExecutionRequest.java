package com.vizorix.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** Request payload for initiating a code execution session. */
@Getter
@Setter
public class ExecutionRequest {

  @NotBlank(message = "sourceCode must not be blank")
  private String sourceCode;

  @NotNull(message = "projectId must not be null") private UUID projectId;
}
