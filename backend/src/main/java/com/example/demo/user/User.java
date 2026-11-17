package com.example.demo.user;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "app_users")
public class User {
    public enum Role { STUDENT, STAFF, ADMIN }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 190)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private Role role = Role.STUDENT;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean mustChangePassword;

    @Column(nullable = false)
    private boolean emailVerified = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected User() {
    }

    public User(String fullName, String email, String passwordHash) {
        this(fullName, email, passwordHash, Role.STUDENT, true);
    }

    public User(String fullName, String email, String passwordHash, Role role, boolean emailVerified) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.emailVerified = emailVerified;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public boolean isMustChangePassword() { return mustChangePassword; }
    public boolean isEmailVerified() { return emailVerified; }
    public Instant getCreatedAt() { return createdAt; }
    public void setPasswordHash(String value) { passwordHash = value; }
    public void setActive(boolean value) { active = value; }
    public void setMustChangePassword(boolean value) { mustChangePassword = value; }
    public void setEmailVerified(boolean value) { emailVerified = value; }
}
