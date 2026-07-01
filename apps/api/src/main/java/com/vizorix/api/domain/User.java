package com.vizorix.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
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

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<Project> projects = new ArrayList<>();
}
