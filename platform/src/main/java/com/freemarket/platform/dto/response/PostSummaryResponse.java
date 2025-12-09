package com.freemarket.platform.dto.response;

import com.freemarket.platform.entity.PostType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonPropertyOrder({
        "id", "type", "title", "description", "location",
        "priceInfo", "tags", "createdAt", "username"
})
public class PostSummaryResponse {
    private UUID id;
    private PostType type;
    private String title;
    private String description;
    private String location;
    private String priceInfo;
    private Set<String> tags;
    private LocalDateTime createdAt;
    private String username;

    // Default constructor
    public PostSummaryResponse() {}

    public PostSummaryResponse(UUID id, PostType type, String title, String description, String location,
                               String priceInfo, Set<String> tags, LocalDateTime createdAt, String username) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.location = location;
        this.priceInfo = priceInfo;
        this.tags = tags;
        this.createdAt = createdAt;
        this.username = username;
    }

    // Utility method for truncated description
    public String getShortDescription() {
        if (description == null) return null;
        return description.length() > 150
                ? description.substring(0, 147) + "..."
                : description;
    }

    // toString for debugging
    @Override
    public String toString() {
        return "PostSummaryResponse{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", description='" + (description != null ? getShortDescription() : "null") + '\'' +
                ", location='" + location + '\'' +
                ", priceInfo='" + priceInfo + '\'' +
                ", tags=" + tags +
                ", createdAt=" + createdAt +
                ", username='" + username + '\'' +
                '}';
    }

    // equals and hashCode for testing
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostSummaryResponse that = (PostSummaryResponse) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}