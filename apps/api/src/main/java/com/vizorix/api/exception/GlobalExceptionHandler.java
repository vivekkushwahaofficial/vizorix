package com.vizorix.api.exception;

import com.vizorix.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller advice class intercepting thrown exceptions and wrapping standard ErrorResponse
 * models.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Intercepts ResourceNotFoundException.
   *
   * @param ex the intercepted resource exception
   * @param request the active servlet request context
   * @return standard error response payload
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), request);
  }

  /**
   * Intercepts AccessDeniedException.
   *
   * @param ex the intercepted access exception
   * @param request the active servlet request context
   * @return standard error response payload
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.FORBIDDEN, "ACCESS_DENIED", ex.getMessage(), request);
  }

  /**
   * Intercepts UserAlreadyExistsException during registration flows.
   *
   * @param ex the duplicate user exception
   * @param request the active servlet request context
   * @return standard error response payload
   */
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
      UserAlreadyExistsException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", ex.getMessage(), request);
  }

  /**
   * Intercepts InvalidCredentialsException during login checks.
   *
   * @param ex the invalid credentials exception
   * @param request the active servlet request context
   * @return standard error response payload
   */
  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentials(
      InvalidCredentialsException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", ex.getMessage(), request);
  }

  /**
   * Intercepts Spring Security's BadCredentialsException during login verification checks.
   *
   * @param ex the bad credentials exception
   * @param request the active servlet request context
   * @return standard error response payload
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(
      BadCredentialsException ex, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username or password", request);
  }

  /**
   * Intercepts IllegalArgumentException.
   *
   * @param ex the intercepted illegal argument exception
   * @param request the active servlet request context
   * @return standard error response payload
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_INPUT", ex.getMessage(), request);
  }

  /**
   * Intercepts validation check errors during serialization bindings.
   *
   * @param ex the intercepted binding errors exception
   * @param request the active servlet request context
   * @return standard error response payload
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String detailedMessage =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
            .collect(Collectors.joining("; "));
    return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", detailedMessage, request);
  }

  /**
   * Intercepts CompilationException during Java code compile checks.
   *
   * @param ex compilation failure exception
   * @param request servlet request context
   * @return structured compiler diagnostics error response
   */
  @ExceptionHandler(CompilationException.class)
  public ResponseEntity<ErrorResponse> handleCompilation(
      CompilationException ex, HttpServletRequest request) {
    String detail = String.join("; ", ex.getDiagnostics());
    return buildResponse(
        HttpStatus.BAD_REQUEST,
        "COMPILATION_FAILED",
        ex.getMessage() + (detail.isEmpty() ? "" : ": " + detail),
        request);
  }

  /**
   * Intercepts general execution engine debugger trace failures.
   *
   * @param ex execution engine exception
   * @param request servlet request context
   * @return standard error response
   */
  @ExceptionHandler(ExecutionEngineException.class)
  public ResponseEntity<ErrorResponse> handleExecutionEngine(
      ExecutionEngineException ex, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR, "EXECUTION_FAILED", ex.getMessage(), request);
  }

  /**
   * Fallback for general unhandled runtime exceptions.
   *
   * @param ex the general runtime exception
   * @param request the active servlet request context
   * @return standard error response payload
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(
      Exception ex, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_SERVER_ERROR",
        "An unexpected error occurred",
        request);
  }

  private ResponseEntity<ErrorResponse> buildResponse(
      HttpStatus status, String errorCode, String message, HttpServletRequest request) {
    ErrorResponse error = new ErrorResponse();
    error.setTimestamp(Instant.now());
    error.setStatus(status.value());
    error.setError(status.getReasonPhrase());
    error.setErrorCode(errorCode);
    error.setMessage(message);
    error.setPath(request.getRequestURI());
    return new ResponseEntity<>(error, status);
  }
}
