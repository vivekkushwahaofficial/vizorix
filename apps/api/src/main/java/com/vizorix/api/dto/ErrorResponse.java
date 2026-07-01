package com.vizorix.api.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/** Standardized HTTP response payload structure for API errors and validation failures. */
@Getter
@Setter
public class ErrorResponse {
  private Instant timestamp;
  private int status;
  private String error;
  private String errorCode;
  private String message;
  private String path;
}
