package com.freemarket.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private MarketActor user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private PostType type;

    @Column(nullable = false, length = 200)
    @Size(min = 5, max = 200)
    @NotNull
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Size(min = 10, max = 5000)
    @NotNull
    private String description;

    @Column(length = 100)
    @Nullable
    private String location;

    @Column(name = "price_info", length = 100)
    @Nullable
    private String priceInfo;

    @Column(name = "contact_info", columnDefinition = "TEXT")
    @Nullable
    private String contactInfo;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @NotNull
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @NotNull
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    @Nullable
    private LocalDateTime expiresAt;

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

    // Default constructor (required by JPA)
    public Post() {
    }

    // Minimal constructor for required fields
    public Post(MarketActor user, PostType type, String title, String description) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.description = description;
        this.isActive = true;
    }

    // Full business constructor
    public Post(MarketActor user, PostType type, String title, String description,
                @Nullable String location, @Nullable String priceInfo,
                @Nullable String contactInfo, @Nullable LocalDateTime expiresAt,
                Set<String> tags) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.description = description;
        this.location = location;
        this.priceInfo = priceInfo;
        this.contactInfo = contactInfo;
        this.expiresAt = expiresAt;
        this.tags = tags != null ? new HashSet<>(tags) : new HashSet<>();
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

    // Builder pattern for fluent creation
    public static PostBuilder builder() {
        return new PostBuilder();
    }

    public static class PostBuilder {
        private final Post post;

        public PostBuilder() {
            this.post = new Post();
            this.post.isActive = true;
        }

        public PostBuilder user(MarketActor user) {
            post.setUser(user);
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

        public PostBuilder isActive(Boolean isActive) {
            post.setIsActive(isActive);
            return this;
        }

        public Post build() {
            // Validate required fields
            if (post.getUser() == null || post.getType() == null ||
                    post.getTitle() == null || post.getDescription() == null) {
                throw new IllegalStateException("Required fields (user, type, title, description) must be set");
            }
            return post;
        }
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    public MarketActor getUser() {
        return user;
    }

    public void setUser(MarketActor user) {
        this.user = user;
        this.updatedAt = LocalDateTime.now();
    }

    public PostType getType() {
        return type;
    }

    public void setType(PostType type) {
        this.type = type;
        this.updatedAt = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    protected void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Nullable
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(@Nullable LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getIsActive() {
        return isActive;
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

    // Utility methods for conversion
    public com.freemarket.platform.dto.response.PostResponse toResponse(
            com.freemarket.platform.dto.response.UserResponse userResponse) {
        return com.freemarket.platform.dto.response.PostResponse.fromEntity(this, userResponse);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", user=" + (user != null ? user.getId() : "null") +
                ", isActive=" + isActive +
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