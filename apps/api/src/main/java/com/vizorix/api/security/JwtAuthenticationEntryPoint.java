package com.vizorix.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vizorix.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Authentication entry point rendering standard JSON ErrorResponse structures for unauthorized
 * calls.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  public JwtAuthenticationEntryPoint() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ErrorResponse error = new ErrorResponse();
    error.setTimestamp(Instant.now());
    error.setStatus(HttpStatus.UNAUTHORIZED.value());
    error.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
    error.setErrorCode("UNAUTHORIZED");
    error.setMessage("Authentication credentials are required to access this resource");
    error.setPath(request.getRequestURI());

    response.getWriter().write(objectMapper.writeValueAsString(error));
  }
}
