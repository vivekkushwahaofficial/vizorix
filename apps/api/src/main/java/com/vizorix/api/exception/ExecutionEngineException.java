package com.vizorix.api.exception;

/** Exception thrown when the execution engine fails to trace a program. */
public class ExecutionEngineException extends RuntimeException {

  /**
   * Constructs the execution engine exception.
   *
   * @param message description detailing the failure
   */
  public ExecutionEngineException(String message) {
    super(message);
  }

  /**
   * Constructs the execution engine exception with a cause.
   *
   * @param message description detailing the failure
   * @param cause the underlying exception that triggered this failure
   */
  public ExecutionEngineException(String message, Throwable cause) {
    super(message, cause);
  }
}
