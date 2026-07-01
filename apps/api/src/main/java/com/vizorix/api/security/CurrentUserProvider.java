package com.vizorix.api.security;

import java.util.UUID;

/** Service provider interface for resolving the active user account ID in scope. */
public interface CurrentUserProvider {

  /**
   * Retrieves the active user's UUID context.
   *
   * @return the UUID of the current user
   */
  UUID getCurrentUserId();
}
