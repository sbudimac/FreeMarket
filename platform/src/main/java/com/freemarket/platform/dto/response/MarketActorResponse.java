package com.freemarket.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarketActorResponse {
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
    public MarketActorResponse() {}

    public MarketActorResponse(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public MarketActorResponse(UUID id, String username, String email, String contactInfo,
                               Boolean isVerified, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.contactInfo = contactInfo;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketActorResponse that = (MarketActorResponse) o;
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
    public static MarketActorResponse fromBasicInfo(UUID id, String username, String email) {
        return new MarketActorResponse(id, username, email);
    }
}