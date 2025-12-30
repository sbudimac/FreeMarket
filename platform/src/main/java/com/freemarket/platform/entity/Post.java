package com.freemarket.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "post")
public class Post {

    @Getter
    @Setter
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_actor_id", nullable = false)
    @NotNull
    private MarketActor marketActor; // CHANGED: user → marketActor

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private PostType type;

    @Getter
    @Column(nullable = false, length = 200)
    @Size(min = 5, max = 200)
    @NotNull
    private String title;

    @Getter
    @Column(nullable = false, columnDefinition = "TEXT")
    @Size(min = 10, max = 5000)
    @NotNull
    private String description;

    @Column(length = 100)
    private String location;

    @Column(name = "price_info", length = 100)
    private String priceInfo;

    @Column(name = "contact_info", columnDefinition = "TEXT")
    private String contactInfo;

    @Getter
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @NotNull
    private LocalDateTime createdAt;

    @Setter
    @Getter
    @UpdateTimestamp
    @Column(name = "updated_at")
    @NotNull
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Getter
    @Column(name = "is_active")
    @NotNull
    private Boolean isActive = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    public Post() {}
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "post_images",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "image_url", columnDefinition = "TEXT")
    @Size(max = 10, message = "Cannot have more than 10 images") // Limit number of images
    @jakarta.validation.constraints.Pattern.List({
            @Pattern(regexp = "^(http|https)://.*$", message = "Image URL must start with http:// or https://"),
            @Pattern(regexp = "^.*\\.(jpg|jpeg|png|gif|webp|bmp)$", message = "Image URL must end with a valid image extension (jpg, jpeg, png, gif, webp, bmp)")
    })
    private Set<String> images = new HashSet<>();

    // Minimal constructor for required fields
    public Post(MarketActor marketActor, PostType type, String title, String description) {
        this.marketActor = marketActor;
        this.type = type;
        this.title = title;
        this.description = description;
        this.isActive = true;
        this.images = new HashSet<>();
    }

    // Full business constructor
    public Post(MarketActor marketActor, PostType type, String title, String description,
                @Nullable String location, @Nullable String priceInfo,
                @Nullable String contactInfo, @Nullable LocalDateTime expiresAt,
                Set<String> tags, @Nullable Set<String> images) { // UPDATED: Added images parameter
        this.marketActor = marketActor;
        this.type = type;
        this.title = title;
        this.description = description;
        this.location = location;
        this.priceInfo = priceInfo;
        this.contactInfo = contactInfo;
        this.expiresAt = expiresAt;
        this.tags = tags != null ? new HashSet<>(tags) : new HashSet<>();
        this.images = images != null ? new HashSet<>(images) : new HashSet<>();
        this.isActive = true;
    }

    // Business logic methods
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isNew() {
        return createdAt != null && createdAt.isAfter(LocalDateTime.now().minusDays(1));
    }

    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }

    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }

    @Nullable
    public String getFirstImageUrl() {
        if (hasImages()) {
            assert images != null;
            return images.iterator().next();
        }
        return null;
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void extendExpiration(LocalDateTime newExpiresAt) {
        this.expiresAt = newExpiresAt;
        this.updatedAt = LocalDateTime.now();
    }

    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new HashSet<>();
        }
        this.tags.add(tag);
        this.updatedAt = LocalDateTime.now();
    }

    public void addTags(Set<String> newTags) {
        if (this.tags == null) {
            this.tags = new HashSet<>();
        }
        this.tags.addAll(newTags);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeTag(String tag) {
        if (this.tags != null) {
            this.tags.remove(tag);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void clearTags() {
        if (this.tags != null) {
            this.tags.clear();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void addImage(String imageUrl) {
        if (this.images == null) {
            this.images = new HashSet<>();
        }
        this.images.add(imageUrl);
        this.updatedAt = LocalDateTime.now();
    }

    public void addImages(Set<String> newImages) {
        if (this.images == null) {
            this.images = new HashSet<>();
        }
        this.images.addAll(newImages);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeImage(String imageUrl) {
        if (this.images != null) {
            this.images.remove(imageUrl);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void clearImages() {
        if (this.images != null) {
            this.images.clear();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean hasValidImages() {
        if (!hasImages()) return true;

        String imageUrlPattern = "^(http|https)://.*\\.(jpg|jpeg|png|gif|webp|bmp)$";
        for (String imageUrl : images) {
            if (!imageUrl.matches(imageUrlPattern)) {
                return false;
            }
        }
        return true;
    }

    public static class PostBuilder {
        private final Post post;

        public PostBuilder() {
            this.post = new Post();
            this.post.isActive = true;
            this.post.images = new HashSet<>();
        }

        public PostBuilder marketActor(MarketActor marketActor) {
            post.setMarketActor(marketActor);
            return this;
        }

        public PostBuilder type(PostType type) {
            post.setType(type);
            return this;
        }

        public PostBuilder title(String title) {
            post.setTitle(title);
            return this;
        }

        public PostBuilder description(String description) {
            post.setDescription(description);
            return this;
        }

        public PostBuilder location(String location) {
            post.setLocation(location);
            return this;
        }

        public PostBuilder priceInfo(String priceInfo) {
            post.setPriceInfo(priceInfo);
            return this;
        }

        public PostBuilder contactInfo(String contactInfo) {
            post.setContactInfo(contactInfo);
            return this;
        }

        public PostBuilder expiresAt(LocalDateTime expiresAt) {
            post.setExpiresAt(expiresAt);
            return this;
        }

        public PostBuilder tags(Set<String> tags) {
            post.setTags(tags);
            return this;
        }

        public PostBuilder images(Set<String> images) {
            post.setImages(images);
            return this;
        }

        public PostBuilder isActive(Boolean isActive) {
            post.setIsActive(isActive);
            return this;
        }

        public Post build() {
            // Validate required fields
            if (post.getMarketActor() == null || post.getType() == null ||
                    post.getTitle() == null || post.getDescription() == null) {
                throw new IllegalStateException("Required fields (marketActor, type, title, description) must be set");
            }

            // Validate images if present
            if (post.hasImages() && !post.hasValidImages()) {
                throw new IllegalStateException("All image URLs must be valid URLs ending with image extensions (jpg, jpeg, png, gif, webp, bmp)");
            }

            return post;
        }
    }

    // Getters and setters
    public void setMarketActor(MarketActor marketActor) { // CHANGED
        this.marketActor = marketActor;
        this.updatedAt = LocalDateTime.now();
    }

    public void setType(PostType type) {
        this.type = type;
        this.updatedAt = LocalDateTime.now();
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    @Nullable
    public String getLocation() {
        return location;
    }

    public void setLocation(@Nullable String location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    @Nullable
    public String getPriceInfo() {
        return priceInfo;
    }

    public void setPriceInfo(@Nullable String priceInfo) {
        this.priceInfo = priceInfo;
        this.updatedAt = LocalDateTime.now();
    }

    @Nullable
    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(@Nullable String contactInfo) {
        this.contactInfo = contactInfo;
        this.updatedAt = LocalDateTime.now();
    }

    protected void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(@Nullable LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
        this.updatedAt = LocalDateTime.now();
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }

    public Set<String> getTags() {
        return new HashSet<>(tags); // Return defensive copy
    }

    public void setTags(Set<String> tags) {
        this.tags = tags != null ? new HashSet<>(tags) : new HashSet<>();
        this.updatedAt = LocalDateTime.now();
    }

    public Set<String> getImages() {
        return new HashSet<>(images); // Return defensive copy
    }

    public void setImages(@Nullable Set<String> images) {
        this.images = images != null ? new HashSet<>(images) : new HashSet<>();
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", marketActor=" + (marketActor != null ? marketActor.getId() : "null") + // CHANGED
                ", isActive=" + isActive +
                ", imagesCount=" + (images != null ? images.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post post)) return false;
        return id != null && id.equals(post.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}