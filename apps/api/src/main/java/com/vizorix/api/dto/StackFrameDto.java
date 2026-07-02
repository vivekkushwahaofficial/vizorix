package com.vizorix.api.dto;

import lombok.Getter;
import lombok.Setter;

/** DTO representing a single call stack frame at an execution step. */
@Getter
@Setter
public class StackFrameDto {

  private String className;
  private String methodName;
  private int lineNumber;
}
