package com.freemarket.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String contactInfo;

    @JsonProperty("isVerified")
    private Boolean isVerified;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Optional statistics fields (for future use)
    private Integer totalPosts;
    private Double averageRating;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastActive;

    // Constructors
    public UserResponse() {}

    public UserResponse(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public UserResponse(UUID id, String username, String email, String contactInfo,
                        Boolean isVerified, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.contactInfo = contactInfo;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
    }

    // Getters and Setters
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

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(Integer totalPosts) {
        this.totalPosts = totalPosts;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    // Builder Pattern
    public static UserResponseBuilder builder() {
        return new UserResponseBuilder();
    }

    public static class UserResponseBuilder {
        private UUID id;
        private String username;
        private String email;
        private String contactInfo;
        private Boolean isVerified;
        private LocalDateTime createdAt;
        private Integer totalPosts;
        private Double averageRating;
        private LocalDateTime lastActive;

        public UserResponseBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserResponseBuilder contactInfo(String contactInfo) {
            this.contactInfo = contactInfo;
            return this;
        }

        public UserResponseBuilder isVerified(Boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }

        public UserResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserResponseBuilder totalPosts(Integer totalPosts) {
            this.totalPosts = totalPosts;
            return this;
        }

        public UserResponseBuilder averageRating(Double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public UserResponseBuilder lastActive(LocalDateTime lastActive) {
            this.lastActive = lastActive;
            return this;
        }

        public UserResponse build() {
            UserResponse response = new UserResponse();
            response.setId(this.id);
            response.setUsername(this.username);
            response.setEmail(this.email);
            response.setContactInfo(this.contactInfo);
            response.setIsVerified(this.isVerified);
            response.setCreatedAt(this.createdAt);
            response.setTotalPosts(this.totalPosts);
            response.setAverageRating(this.averageRating);
            response.setLastActive(this.lastActive);
            return response;
        }
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResponse that = (UserResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(username, that.username) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }

    // toString
    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", isVerified=" + isVerified +
                ", createdAt=" + createdAt +
                ", totalPosts=" + totalPosts +
                ", averageRating=" + averageRating +
                ", lastActive=" + lastActive +
                '}';
    }

    // Convenience static factory methods
    public static UserResponse fromBasicInfo(UUID id, String username, String email) {
        return new UserResponse(id, username, email);
    }

    public static UserResponse createMinimal(UUID id, String username, String email,
                                             Boolean isVerified, LocalDateTime createdAt) {
        return UserResponse.builder()
                .id(id)
                .username(username)
                .email(email)
                .isVerified(isVerified)
                .createdAt(createdAt)
                .build();
    }
}