package com.vizorix.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Service managing JSON Web Token (JWT) signatures operations and parsing claims. */
@Service
public class JwtService {

  private final SecretKey signingKey;
  private final long accessTokenExpirationMs;
  private final long refreshTokenExpirationMs;

  /** Constructs the service pulling values from configuration contexts. */
  public JwtService(
      @Value("${vizorix.security.jwt.secret}") String secret,
      @Value("${vizorix.security.jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
      @Value("${vizorix.security.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTokenExpirationMs = accessTokenExpirationMs;
    this.refreshTokenExpirationMs = refreshTokenExpirationMs;
  }

  /** Generates a secure Access JWT carrying username and permissions claims. */
  public String generateAccessToken(UUID userId, String username, List<String> roles) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("uid", userId.toString());
    claims.put("roles", roles);

    return Jwts.builder()
        .claims(claims)
        .subject(username)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
        .signWith(signingKey)
        .compact();
  }

  /** Generates a secure Refresh JWT. */
  public String generateRefreshToken(String username) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("tokenType", "REFRESH");

    return Jwts.builder()
        .claims(claims)
        .subject(username)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
        .signWith(signingKey)
        .compact();
  }

  /** Validates a JWT matches the user credentials and expiration metrics. */
  public boolean validateToken(String token, String username) {
    String extractedUsername = extractUsername(token);
    return extractedUsername.equals(username) && !isTokenExpired(token);
  }

  /** Validates a refresh token payload matches refresh claims constraints. */
  public boolean validateRefreshToken(String token) {
    try {
      Claims claims = extractAllClaims(token);
      String tokenType = claims.get("tokenType", String.class);
      return "REFRESH".equals(tokenType) && !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  /** Extracts the user UUID from the token payload. */
  public UUID extractUserId(String token) {
    String uid = extractAllClaims(token).get("uid", String.class);
    return uid != null ? UUID.fromString(uid) : null;
  }

  /** Extracts user roles authorities list from the token claims. */
  @SuppressWarnings("unchecked")
  public List<String> extractRoles(String token) {
    return extractAllClaims(token).get("roles", List.class);
  }

  private boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
  }
}
