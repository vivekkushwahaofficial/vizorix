package com.vizorix.api.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** Output data details response model representing registered user account details. */
@Getter
@Setter
public class UserResponse {
  private UUID id;
  private String email;
  private String username;
  private Set<String> roles;
  private Instant createdAt;
  private Instant updatedAt;
}
