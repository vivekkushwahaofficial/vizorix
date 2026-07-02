package com.vizorix.api.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Response returned immediately after a successful execution submission. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionInitResponse {

  /** Unique identifier for the execution session — used to fetch individual steps. */
  private UUID executionId;

  /** Total number of traced steps available to fetch. */
  private int totalSteps;
}
