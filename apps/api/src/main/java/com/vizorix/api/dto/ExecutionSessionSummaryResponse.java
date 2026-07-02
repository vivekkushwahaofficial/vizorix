package com.vizorix.api.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** DTO representing the summary of an execution session. */
@Getter
@Setter
public class ExecutionSessionSummaryResponse {
  private UUID executionId;
  private int totalSteps;
  private String status;
  private String errorMessage;
  private Long compileTimeMs;
  private Long executionTimeMs;
}
