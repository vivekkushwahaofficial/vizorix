package com.vizorix.api.exception;

/** Exception thrown when a requested database entity resource is not found. */
public class ResourceNotFoundException extends RuntimeException {

  /**
   * Constructs the resource exception.
   *
   * @param message description detailing the failure
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
