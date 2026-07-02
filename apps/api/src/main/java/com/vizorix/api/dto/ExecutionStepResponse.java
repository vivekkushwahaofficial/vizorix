package com.vizorix.api.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** DTO representing the full detail of a single execution step. */
@Getter
@Setter
public class ExecutionStepResponse {

  private int step;
  private int totalSteps;
  private int lineNumber;
  private String className;
  private String methodName;
  private String stdout;
  private String stderr;
  private List<VariableSnapshotDto> variables;
  private List<StackFrameDto> callStack;
}
