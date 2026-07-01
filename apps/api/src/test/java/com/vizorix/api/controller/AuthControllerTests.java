package com.vizorix.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizorix.api.config.SecurityConfig;
import com.vizorix.api.dto.LoginRequest;
import com.vizorix.api.dto.RefreshTokenRequest;
import com.vizorix.api.dto.RegisterRequest;
import com.vizorix.api.dto.TokenResponse;
import com.vizorix.api.dto.UserResponse;
import com.vizorix.api.exception.InvalidCredentialsException;
import com.vizorix.api.exception.UserAlreadyExistsException;
import com.vizorix.api.security.CustomUserDetailsService;
import com.vizorix.api.security.JwtAuthenticationEntryPoint;
import com.vizorix.api.security.JwtAuthenticationFilter;
import com.vizorix.api.security.JwtService;
import com.vizorix.api.service.AuthService;
import java.util.Collections;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Controller tests for Auth REST operations using MockMvc.
 *
 * <p>Auth endpoints are configured {@code permitAll()} in {@link SecurityConfig}, so no mock user
 * is required. {@code @Import(SecurityConfig.class)} ensures the custom filter chain (and its
 * {@code permitAll} rules) is active in the slice context.
 */
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class})
class AuthControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthService authService;

  // Required by JwtAuthenticationFilter loaded via SecurityFilterChain in WebMvcTest
  @MockBean private JwtService jwtService;

  @MockBean private CustomUserDetailsService customUserDetailsService;

  /** Tests that a valid registration request returns 201. */
  @Test
  void testRegisterSuccess() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("newuser@vizorix.com");
    request.setUsername("newuser");
    request.setPassword("securePassword123");

    UserResponse response = new UserResponse();
    response.setId(UUID.randomUUID());
    response.setEmail("newuser@vizorix.com");
    response.setUsername("newuser");
    response.setRoles(Collections.singleton("USER"));

    when(authService.register(any(RegisterRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("newuser"))
        .andExpect(jsonPath("$.email").value("newuser@vizorix.com"));
  }

  /** Tests registration validation constraint bounds checking. */
  @Test
  void testRegisterValidationFailure() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("bademail");
    request.setUsername("us");
    request.setPassword("short"); // Size must be min 8

    mockMvc
        .perform(
            post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.message").value(Matchers.containsString("email")))
        .andExpect(jsonPath("$.message").value(Matchers.containsString("username")))
        .andExpect(jsonPath("$.message").value(Matchers.containsString("password")));
  }

  /** Tests registration duplicate name/email rejection. */
  @Test
  void testRegisterDuplicate() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("dup@vizorix.com");
    request.setUsername("duplicate");
    request.setPassword("securePassword123");

    when(authService.register(any(RegisterRequest.class)))
        .thenThrow(new UserAlreadyExistsException("Username 'duplicate' is already taken"));

    mockMvc
        .perform(
            post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("USER_ALREADY_EXISTS"))
        .andExpect(jsonPath("$.message").value("Username 'duplicate' is already taken"));
  }

  /** Tests credentials checking logins. */
  @Test
  void testLoginSuccess() throws Exception {
    LoginRequest request = new LoginRequest();
    request.setUsername("validuser");
    request.setPassword("password");

    TokenResponse response =
        new TokenResponse("access_token_123", "refresh_token_123", "Bearer", 3600L);

    when(authService.login(any(LoginRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("access_token_123"))
        .andExpect(jsonPath("$.refreshToken").value("refresh_token_123"));
  }

  /** Tests failed credentials login returns 401. */
  @Test
  void testLoginCredentialsFailure() throws Exception {
    LoginRequest request = new LoginRequest();
    request.setUsername("invaliduser");
    request.setPassword("wrong");

    when(authService.login(any(LoginRequest.class)))
        .thenThrow(new InvalidCredentialsException("Invalid username or password"));

    mockMvc
        .perform(
            post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"))
        .andExpect(jsonPath("$.message").value("Invalid username or password"));
  }

  /** Tests token refresh success. */
  @Test
  void testRefreshSuccess() throws Exception {
    RefreshTokenRequest request = new RefreshTokenRequest();
    request.setRefreshToken("valid_refresh_token");

    TokenResponse response =
        new TokenResponse("new_access_token", "valid_refresh_token", "Bearer", 3600L);

    when(authService.refresh(any(RefreshTokenRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("new_access_token"))
        .andExpect(jsonPath("$.refreshToken").value("valid_refresh_token"));
  }
}
