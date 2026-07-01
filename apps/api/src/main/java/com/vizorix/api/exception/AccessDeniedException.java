package com.vizorix.api.exception;

/** Exception thrown when a user attempts to access or modify a resource without permissions. */
public class AccessDeniedException extends RuntimeException {

  /**
   * Constructs the access exception.
   *
   * @param message description detailing the failure
   */
  public AccessDeniedException(String message) {
    super(message);
  }
}
