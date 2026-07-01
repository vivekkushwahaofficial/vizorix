package com.vizorix.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Request credentials container structure for user login authentication. */
@Getter
@Setter
public class LoginRequest {

  @NotBlank private String username;

  @NotBlank private String password;
}
