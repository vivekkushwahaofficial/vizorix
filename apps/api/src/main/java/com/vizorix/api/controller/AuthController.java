package com.vizorix.api.controller;

import com.vizorix.api.dto.LoginRequest;
import com.vizorix.api.dto.RefreshTokenRequest;
import com.vizorix.api.dto.RegisterRequest;
import com.vizorix.api.dto.TokenResponse;
import com.vizorix.api.dto.UserResponse;
import com.vizorix.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST controller coordinating HTTP requests for user authentication. */
@RestController
@RequestMapping("/api/auth")
@Tag(
    name = "Authentication",
    description = "Endpoints for user registration and JWT authentication")
public class AuthController {

  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /** Registers a new user account in the system. */
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Register user",
      description = "Creates a new user account profile in the system with standard USER role.")
  @ApiResponse(responseCode = "201", description = "User registered successfully")
  @ApiResponse(responseCode = "400", description = "Invalid registration payload properties")
  @ApiResponse(responseCode = "409", description = "Username or Email already registered")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
    UserResponse response = authService.register(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /** Authenticates user credentials and generates access/refresh tokens. */
  @PostMapping("/login")
  @Operation(
      summary = "Login user",
      description = "Authenticates user credentials and returns a secure JWT Token Response.")
  @ApiResponse(responseCode = "200", description = "Login successful, tokens returned")
  @ApiResponse(responseCode = "400", description = "Invalid credentials payload format")
  @ApiResponse(responseCode = "401", description = "Invalid username or password credentials")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
    TokenResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  /** Refreshes an expired access token using a valid refresh token. */
  @PostMapping("/refresh")
  @Operation(
      summary = "Refresh token",
      description = "Validates a refresh token and generates a new access token.")
  @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request format")
  @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
    TokenResponse response = authService.refresh(request);
    return ResponseEntity.ok(response);
  }
}
//
