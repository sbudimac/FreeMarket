package com.freemarket.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // Token expiration in seconds
    private String refreshToken; // Optional: for refresh token flow
    private String scope = "read write"; // Optional: OAuth2 scope

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt; // Exact expiration timestamp

    private UserResponse user;

    // Constructors
    public AuthResponse() {}

    // Basic constructor
    public AuthResponse(String accessToken, UserResponse user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    // Full constructor
    public AuthResponse(String accessToken, UserResponse user, Long expiresIn,
                        LocalDateTime expiresAt, String refreshToken) {
        this.accessToken = accessToken;
        this.user = user;
        this.expiresIn = expiresIn;
        this.expiresAt = expiresAt;
        this.refreshToken = refreshToken;
    }

    // Constructor with expiresIn
    public AuthResponse(String accessToken, UserResponse user, Long expiresIn) {
        this.accessToken = accessToken;
        this.user = user;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    // Builder pattern for fluent creation (optional but useful)
    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {
        private String accessToken;
        private UserResponse user;
        private Long expiresIn;
        private LocalDateTime expiresAt;
        private String refreshToken;
        private String tokenType = "Bearer";
        private String scope = "read write";

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder user(UserResponse user) {
            this.user = user;
            return this;
        }

        public Builder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public AuthResponse build() {
            AuthResponse response = new AuthResponse();
            response.setAccessToken(this.accessToken);
            response.setUser(this.user);
            response.setExpiresIn(this.expiresIn);
            response.setExpiresAt(this.expiresAt);
            response.setRefreshToken(this.refreshToken);
            response.setTokenType(this.tokenType);
            response.setScope(this.scope);
            return response;
        }
    }

    // Utility methods
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean hasRefreshToken() {
        return refreshToken != null && !refreshToken.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "accessToken='[PROTECTED]'" +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshToken='" + (refreshToken != null ? "[PROTECTED]" : "null") + '\'' +
                ", scope='" + scope + '\'' +
                ", expiresAt=" + expiresAt +
                ", user=" + user +
                '}';
    }
}