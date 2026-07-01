package com.vizorix.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vizorix.api.config.SecurityConfig;
import com.vizorix.api.domain.User;
import com.vizorix.api.domain.enums.Role;
import com.vizorix.api.security.CurrentUserProvider;
import com.vizorix.api.security.CustomUserDetailsService;
import com.vizorix.api.security.JwtAuthenticationEntryPoint;
import com.vizorix.api.security.JwtAuthenticationFilter;
import com.vizorix.api.security.JwtService;
import com.vizorix.api.security.model.AuthenticatedUser;
import com.vizorix.api.service.ProjectService;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration web tests evaluating REST endpoints access security constraints.
 *
 * <p>{@code @Import(SecurityConfig.class)} loads our custom {@link SecurityConfig} so that the
 * {@link com.vizorix.api.security.JwtAuthenticationEntryPoint} is registered and returns our
 * standardised JSON {@link com.vizorix.api.dto.ErrorResponse} for 401 responses.
 */
@WebMvcTest(ProjectController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class})
class SecurityIntegrationTests {

  @Autowired private MockMvc mockMvc;

  @MockBean private ProjectService projectService;

  @MockBean private CurrentUserProvider currentUserProvider;

  @MockBean private JwtService jwtService;

  @MockBean private CustomUserDetailsService userDetailsService;

  private final UUID userId = UUID.randomUUID();

  /** Tests that calling secured endpoints without a token returns HTTP 401 with JSON body. */
  @Test
  void testSecurePathAccessDeniedWithoutToken() throws Exception {
    mockMvc
        .perform(get("/api/projects"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"))
        .andExpect(
            jsonPath("$.message")
                .value("Authentication credentials are required to access this resource"));
  }

  /**
   * Tests that a malformed Bearer token (which causes JwtService to return null username) triggers
   * HTTP 401 with a JSON error body from {@link
   * com.vizorix.api.security.JwtAuthenticationEntryPoint}.
   */
  @Test
  void testSecurePathAccessDeniedWithMalformedToken() throws Exception {
    // The mock JwtService returns null by default for unknown inputs — the filter skips setting
    // auth, and Spring Security invokes JwtAuthenticationEntryPoint which writes our JSON 401.
    mockMvc
        .perform(get("/api/projects").header(HttpHeaders.AUTHORIZATION, "Bearer malformed_token"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
  }

  /** Tests that providing a valid Bearer token permits endpoint access. */
  @Test
  void testSecurePathAccessSuccessWithValidToken() throws Exception {
    User user = new User();
    user.setId(userId);
    user.setUsername("activeuser");
    user.setEmail("active@vizorix.com");
    user.setPasswordHash("hash");
    user.setRoles(new HashSet<>(Collections.singletonList(Role.USER)));
    AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);

    when(jwtService.extractUsername("valid_token")).thenReturn("activeuser");
    when(jwtService.validateToken("valid_token", "activeuser")).thenReturn(true);
    when(userDetailsService.loadUserByUsername("activeuser")).thenReturn(authenticatedUser);

    when(currentUserProvider.getCurrentUserId()).thenReturn(userId);
    when(projectService.listProjects(eq(userId), any()))
        .thenReturn(new PageImpl<>(Collections.emptyList()));

    mockMvc
        .perform(get("/api/projects").header(HttpHeaders.AUTHORIZATION, "Bearer valid_token"))
        .andExpect(status().isOk());
  }
}
