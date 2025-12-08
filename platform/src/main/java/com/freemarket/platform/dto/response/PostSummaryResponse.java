package com.freemarket.platform.dto.response;

import com.freemarket.platform.entity.PostType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@JsonPropertyOrder({
        "id", "type", "title", "description", "location",
        "priceInfo", "tags", "images", "createdAt", "username" // UPDATED: Added images
})
public class PostSummaryResponse {
    private UUID id;
    private PostType type;
    private String title;
    private String description;
    private String location;
    private String priceInfo;
    private Set<String> tags;
    private Set<String> images; // NEW: Images field
    private LocalDateTime createdAt;
    private String username;

    // Default constructor
    public PostSummaryResponse() {}

    // Builder constructor
    private PostSummaryResponse(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.title = builder.title;
        this.description = builder.description;
        this.location = builder.location;
        this.priceInfo = builder.priceInfo;
        this.tags = builder.tags;
        this.images = builder.images; // NEW: Initialize images
        this.createdAt = builder.createdAt;
        this.username = builder.username;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PostType getType() { return type; }
    public void setType(PostType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPriceInfo() { return priceInfo; }
    public void setPriceInfo(String priceInfo) { this.priceInfo = priceInfo; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public Set<String> getImages() { return images; }
    public void setImages(Set<String> images) { this.images = images; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // Utility method for truncated description
    public String getShortDescription() {
        if (description == null) return null;
        return description.length() > 150
                ? description.substring(0, 147) + "..."
                : description;
    }

    // Get first image URL for thumbnails
    public String getFirstImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.iterator().next();
        }
        return null;
    }

    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private PostType type;
        private String title;
        private String description;
        private String location;
        private String priceInfo;
        private Set<String> tags;
        private Set<String> images; // NEW: Images builder field
        private LocalDateTime createdAt;
        private String username;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder type(PostType type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder priceInfo(String priceInfo) {
            this.priceInfo = priceInfo;
            return this;
        }

        public Builder tags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        // NEW: Images builder method
        public Builder images(Set<String> images) {
            this.images = images;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public PostSummaryResponse build() {
            return new PostSummaryResponse(this);
        }
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
                ", imagesCount=" + (images != null ? images.size() : 0) + // NEW: Include image count
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

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}