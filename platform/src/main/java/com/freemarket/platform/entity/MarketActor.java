package com.freemarket.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Entity
@Table(name = "market_actor") // Changed from "market_actors" to match SQL
public class MarketActor {

    @Setter
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

    @Setter
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Setter
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Setter
    @OneToMany(mappedBy = "marketActor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "market_actor_roles",
        joinColumns = @JoinColumn(name = "market_actor_id")
    )
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    public MarketActor() {}

    // Convenience constructor without ID and timestamps
    public MarketActor(String username, String email, String passwordHash, String contactInfo) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.contactInfo = contactInfo;
    }

    // Business methods
    public void verify() {
        this.isVerified = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
        this.updatedAt = LocalDateTime.now();
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    // Getters and setters
    public void setUsername(String username) {
        this.username = username;
        this.updatedAt = LocalDateTime.now();
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = LocalDateTime.now();
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
        this.updatedAt = LocalDateTime.now();
    }

    public void setIsVerified(Boolean verified) {
        isVerified = verified;
        this.updatedAt = LocalDateTime.now();
    }

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