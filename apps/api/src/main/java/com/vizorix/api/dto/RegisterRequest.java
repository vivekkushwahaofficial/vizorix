package com.vizorix.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Request payload structure for user account registration. */
@Getter
@Setter
public class RegisterRequest {

  @NotBlank @Email private String email;

  @NotBlank
  @Size(min = 3, max = 50)
  private String username;

  @NotBlank
  @Size(min = 8, max = 128)
  private String password;
}
