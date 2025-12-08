package com.freemarket.platform.dto.response;

import com.freemarket.platform.entity.PostType;
import org.springframework.lang.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class PostResponse {
    @NotNull
    private UUID id;

    @NotNull
    private PostType type;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @Nullable
    private String location;

    @Nullable
    private String priceInfo;

    @Nullable
    private String contactInfo;

    private Set<String> tags;

    @Nullable
    private Set<@Pattern(regexp = "^(http|https)://.*\\.(jpg|jpeg|png|gif|webp|bmp)$",
            message = "Invalid image URL format") String> images;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Nullable
    private LocalDateTime expiresAt;

    @NotNull
    private Boolean isActive;

    @NotNull
    private MarketActorResponse user;

    // Constructors
    public PostResponse() {}

    public PostResponse(UUID id, PostType type, String title, String description,
                        MarketActorResponse user, LocalDateTime createdAt, LocalDateTime updatedAt,
                        Boolean isActive) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
        this.tags = Set.of();
        this.images = Set.of(); // NEW: Initialize empty set
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

    @Nullable
    public String getLocation() { return location; }
    public void setLocation(@Nullable String location) { this.location = location; }

    @Nullable
    public String getPriceInfo() { return priceInfo; }
    public void setPriceInfo(@Nullable String priceInfo) { this.priceInfo = priceInfo; }

    @Nullable
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(@Nullable String contactInfo) { this.contactInfo = contactInfo; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    @Nullable
    public Set<String> getImages() { return images; }
    public void setImages(@Nullable Set<String> images) { this.images = images; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Nullable
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(@Nullable LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public MarketActorResponse getUser() { return user; }
    public void setUser(MarketActorResponse user) { this.user = user; }

    // Derived fields (computed properties)
    public Boolean getIsExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public Boolean getIsNew() {
        return createdAt.isAfter(LocalDateTime.now().minusDays(1));
    }

    public Boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }

    public Boolean hasImages() {
        return images != null && !images.isEmpty();
    }

    // Get first image URL for thumbnails
    @Nullable
    public String getFirstImageUrl() {
        if (hasImages()) {
            assert images != null;
            return images.iterator().next();
        }
        return null;
    }

    // Builder pattern for easy creation
    public static PostResponseBuilder builder() {
        return new PostResponseBuilder();
    }

    public static class PostResponseBuilder {
        private final PostResponse response;

        public PostResponseBuilder() {
            this.response = new PostResponse();
        }

        public PostResponseBuilder id(UUID id) {
            response.setId(id);
            return this;
        }

        public PostResponseBuilder type(PostType type) {
            response.setType(type);
            return this;
        }

        public PostResponseBuilder title(String title) {
            response.setTitle(title);
            return this;
        }

        public PostResponseBuilder description(String description) {
            response.setDescription(description);
            return this;
        }

        public PostResponseBuilder location(String location) {
            response.setLocation(location);
            return this;
        }

        public PostResponseBuilder priceInfo(String priceInfo) {
            response.setPriceInfo(priceInfo);
            return this;
        }

        public PostResponseBuilder contactInfo(String contactInfo) {
            response.setContactInfo(contactInfo);
            return this;
        }

        public PostResponseBuilder tags(Set<String> tags) {
            response.setTags(tags);
            return this;
        }

        public PostResponseBuilder images(Set<String> images) {
            response.setImages(images);
            return this;
        }

        public PostResponseBuilder createdAt(LocalDateTime createdAt) {
            response.setCreatedAt(createdAt);
            return this;
        }

        public PostResponseBuilder updatedAt(LocalDateTime updatedAt) {
            response.setUpdatedAt(updatedAt);
            return this;
        }

        public PostResponseBuilder expiresAt(LocalDateTime expiresAt) {
            response.setExpiresAt(expiresAt);
            return this;
        }

        public PostResponseBuilder isActive(Boolean isActive) {
            response.setIsActive(isActive);
            return this;
        }

        public PostResponseBuilder user(MarketActorResponse user) {
            response.setUser(user);
            return this;
        }

        public PostResponse build() {
            return response;
        }
    }

    // Utility method for conversion from Entity
    public static PostResponse fromEntity(com.freemarket.platform.entity.Post post,
                                          MarketActorResponse marketActorResponse) {
        return PostResponse.builder()
                .id(post.getId())
                .type(post.getType())
                .title(post.getTitle())
                .description(post.getDescription())
                .location(post.getLocation())
                .priceInfo(post.getPriceInfo())
                .contactInfo(post.getContactInfo())
                .tags(post.getTags())
                .images(post.getImages()) // NEW: Include images
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .expiresAt(post.getExpiresAt())
                .isActive(post.getIsActive())
                .user(marketActorResponse)
                .build();
    }

    @Override
    public String toString() {
        return "PostResponse{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", isActive=" + isActive +
                ", imagesCount=" + (images != null ? images.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostResponse that = (PostResponse) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}