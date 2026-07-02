package com.vizorix.api.dto;

import lombok.Getter;
import lombok.Setter;

/** DTO representing a single variable state snapshot at an execution step. */
@Getter
@Setter
public class VariableSnapshotDto {

  private String name;
  private String value;
  private String type;
  private String scope;
}
