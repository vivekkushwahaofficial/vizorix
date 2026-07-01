package com.vizorix.api.exception;

/**
 * Exception thrown when a user registration fails because the email or username is already taken.
 */
public class UserAlreadyExistsException extends RuntimeException {

  /**
   * Constructs the exception.
   *
   * @param message description detailing the failure
   */
  public UserAlreadyExistsException(String message) {
    super(message);
  }
}
