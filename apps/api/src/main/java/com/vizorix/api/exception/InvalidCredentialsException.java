package com.vizorix.api.exception;

/** Exception thrown when user login credentials fail authentication. */
public class InvalidCredentialsException extends RuntimeException {

  /**
   * Constructs the exception.
   *
   * @param message description detailing the failure
   */
  public InvalidCredentialsException(String message) {
    super(message);
  }
}
