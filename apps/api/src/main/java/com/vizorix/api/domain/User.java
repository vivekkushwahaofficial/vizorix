package com.vizorix.api.domain;

import com.vizorix.api.domain.enums.Role;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/** Domain entity representing a user account in the Vizorix platform. */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

  @NotBlank
  @Email
  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @NotBlank
  @Size(min = 3, max = 50)
  @Column(name = "username", unique = true, nullable = false)
  private String username;

  @NotBlank
  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private Set<Role> roles = new HashSet<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<Project> projects = new ArrayList<>();
}
