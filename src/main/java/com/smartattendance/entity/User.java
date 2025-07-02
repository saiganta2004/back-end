package com.smartattendance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "username"),
           @UniqueConstraint(columnNames = "email")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(min = 3, max = 50) // <-- Change max to 50 (or same as RegisterRequest)
  @Column(length = 50)     // <-- Also update column length if needed
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 120)
  private String password;

  // These are the extra fields from your registration form
  private String name;
  private String studentId;

  @Column(name = "face_registered")
  private boolean faceRegistered;

  @Column(name = "face_encoding", columnDefinition="TEXT")
  private String faceEmbedding;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles",
             joinColumns = @JoinColumn(name = "user_id"),
             inverseJoinColumns = @JoinColumn(name = "role_id"))
  @Builder.Default
  private Set<Role> roles = new HashSet<>();

  // This is the constructor your AuthController needs
  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }
}
