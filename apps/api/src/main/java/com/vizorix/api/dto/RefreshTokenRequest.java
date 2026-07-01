package com.vizorix.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Request payload structure containing refresh token to request a new access token. */
@Getter
@Setter
public class RefreshTokenRequest {

  @NotBlank private String refreshToken;
}
