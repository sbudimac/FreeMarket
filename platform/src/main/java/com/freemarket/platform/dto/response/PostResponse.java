package com.freemarket.platform.dto.response;

import com.freemarket.platform.entity.Post;
import com.freemarket.platform.entity.PostType;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class PostResponse {
    @NotNull
    private UUID id;

    @NotNull
    private PostType type;

    @NotNull
    private String title;

    @NotNull
    private String description;

    private String location;

    private String priceInfo;

    private String contactInfo;

    private Set<String> tags;

    private Set<@Pattern(regexp = "^(http|https)://.*\\.(jpg|jpeg|png|gif|webp|bmp)$",
            message = "Invalid image URL format") String> images;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

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

    public String getFirstImageUrl() {
        if (hasImages()) {
            assert images != null;
            return images.iterator().next();
        }
        return null;
    }

    public static PostResponse fromEntity(Post post, MarketActorResponse marketActorResponse) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setType(post.getType());
        response.setTitle(post.getTitle());
        response.setDescription(post.getDescription());
        response.setLocation(post.getLocation());
        response.setPriceInfo(post.getPriceInfo());
        response.setContactInfo(post.getContactInfo());
        response.setTags(post.getTags());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setExpiresAt(post.getExpiresAt());
        response.setIsActive(post.getIsActive());
        response.setUser(marketActorResponse);
        return response;
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