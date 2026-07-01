package com.vizorix.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Output token details response schema. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
  private String accessToken;
  private String refreshToken;
  private String tokenType = "Bearer";
  private long expiresIn;
}
