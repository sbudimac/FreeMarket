package com.freemarket.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "market_actors")
public class MarketActor {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 500)
    private String contactInfo;

    @Column(nullable = false)
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Required by JPA
    public MarketActor() {
    }

    // Convenience constructor without ID and createdAt (for creating new entities)
    public MarketActor(String username, String email, String passwordHash, String contactInfo) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.contactInfo = contactInfo;
        // isVerified defaults to false
        // createdAt will be set by @CreationTimestamp
    }

    // Full constructor (mainly for testing)
    public MarketActor(UUID id, String username, String email, String passwordHash,
                       String contactInfo, Boolean isVerified, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.contactInfo = contactInfo;
        this.isVerified = isVerified != null ? isVerified : false;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    // Consistent naming for boolean field
    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean verified) {
        isVerified = verified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Optional: toString for debugging
    @Override
    public String toString() {
        return "MarketActor{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isVerified=" + isVerified +
                ", createdAt=" + createdAt +
                '}';
    }
}
