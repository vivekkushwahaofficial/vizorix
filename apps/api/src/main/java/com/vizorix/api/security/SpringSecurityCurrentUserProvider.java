package com.vizorix.api.security;

import com.vizorix.api.exception.AccessDeniedException;
import com.vizorix.api.security.model.AuthenticatedUser;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Spring Security context-based implementation of CurrentUserProvider. Resolves the authenticated
 * user ID from the security context principal.
 */
@Component
public class SpringSecurityCurrentUserProvider implements CurrentUserProvider {

  @Override
  public UUID getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new AccessDeniedException("User is not authenticated");
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof AuthenticatedUser authenticatedUser) {
      return authenticatedUser.getId();
    }
    throw new AccessDeniedException("Authentication principal is invalid");
  }
}
