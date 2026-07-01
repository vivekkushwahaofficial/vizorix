package com.vizorix.api.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Request header-based implementation of CurrentUserProvider. Extracts the user UUID from the
 * 'X-User-Id' HTTP header.
 */
@Component
public class HeaderCurrentUserProvider implements CurrentUserProvider {

  private final HttpServletRequest request;

  @Autowired
  public HeaderCurrentUserProvider(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public UUID getCurrentUserId() {
    String headerValue = request.getHeader("X-User-Id");
    if (headerValue == null || headerValue.isBlank()) {
      throw new IllegalArgumentException("Required request header 'X-User-Id' is missing");
    }
    try {
      return UUID.fromString(headerValue.trim());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Header 'X-User-Id' does not represent a valid UUID format");
    }
  }
}
