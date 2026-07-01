package com.vizorix.api.security.model;

import com.vizorix.api.domain.User;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Details mapping object wrapping domain User properties inside Spring Security context. */
public class AuthenticatedUser implements UserDetails {

  private final UUID id;
  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;

  /**
   * Constructs the authenticated user context from a User entity.
   *
   * @param user the domain User entity
   */
  public AuthenticatedUser(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.password = user.getPasswordHash();
    this.authorities =
        user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toSet());
  }

  public UUID getId() {
    return id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
