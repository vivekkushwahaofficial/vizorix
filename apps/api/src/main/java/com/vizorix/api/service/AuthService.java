package com.vizorix.api.service;

import com.vizorix.api.domain.User;
import com.vizorix.api.domain.enums.Role;
import com.vizorix.api.dto.LoginRequest;
import com.vizorix.api.dto.RefreshTokenRequest;
import com.vizorix.api.dto.RegisterRequest;
import com.vizorix.api.dto.TokenResponse;
import com.vizorix.api.dto.UserResponse;
import com.vizorix.api.exception.InvalidCredentialsException;
import com.vizorix.api.exception.UserAlreadyExistsException;
import com.vizorix.api.repository.UserRepository;
import com.vizorix.api.security.JwtService;
import com.vizorix.api.security.model.AuthenticatedUser;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service orchestrating registry, login, and JWT token refresh processing logic. */
@Service
@Transactional
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final long accessTokenExpirationMs;

  /** Constructs the authentication service linking Spring Security beans. */
  @Autowired
  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      @Value("${vizorix.security.jwt.access-token-expiration-ms}") long accessTokenExpirationMs) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.accessTokenExpirationMs = accessTokenExpirationMs;
  }

  /** Registers a new user account in the system. */
  public UserResponse register(RegisterRequest request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new UserAlreadyExistsException(
          "Username '" + request.getUsername() + "' is already taken");
    }
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new UserAlreadyExistsException(
          "Email '" + request.getEmail() + "' is already registered");
    }

    User user = new User();
    user.setUsername(request.getUsername().trim());
    user.setEmail(request.getEmail().trim().toLowerCase());
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    user.setRoles(new HashSet<>(Collections.singletonList(Role.USER)));

    User savedUser = userRepository.save(user);

    UserResponse response = new UserResponse();
    response.setId(savedUser.getId());
    response.setEmail(savedUser.getEmail());
    response.setUsername(savedUser.getUsername());
    response.setRoles(savedUser.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
    response.setCreatedAt(savedUser.getCreatedAt());
    response.setUpdatedAt(savedUser.getUpdatedAt());
    return response;
  }

  /** Authenticates user credentials and generates access/refresh tokens. */
  public TokenResponse login(LoginRequest request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
    List<String> roles =
        principal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    String accessToken =
        jwtService.generateAccessToken(principal.getId(), principal.getUsername(), roles);
    String refreshToken = jwtService.generateRefreshToken(principal.getUsername());

    return new TokenResponse(accessToken, refreshToken, "Bearer", accessTokenExpirationMs);
  }

  /** Refreshes an expired access token using a valid refresh token. */
  @Transactional(readOnly = true)
  public TokenResponse refresh(RefreshTokenRequest request) {
    String refreshToken = request.getRefreshToken();
    if (!jwtService.validateRefreshToken(refreshToken)) {
      throw new InvalidCredentialsException("Invalid or expired refresh token");
    }

    String username = jwtService.extractUsername(refreshToken);
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new InvalidCredentialsException("User not found for provided refresh token"));

    List<String> roles =
        user.getRoles().stream().map(role -> "ROLE_" + role.name()).collect(Collectors.toList());

    String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), roles);

    return new TokenResponse(newAccessToken, refreshToken, "Bearer", accessTokenExpirationMs);
  }
}
