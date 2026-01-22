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
import java.util.*;

@Entity
@Table(name = "post")
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_actor_id", nullable = false)
    @NotNull
    private MarketActor marketActor;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    @NotNull
    private PostCategory category;

    @Column(nullable = false, length = 200)
    @Size(min = 5, max = 200)
    @NotNull
    private String title;

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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    @NotNull
    private Boolean isActive = true;

    @Column(name = "view_count", nullable = false)
    @NotNull
    private Long viewCount = 0L;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "currency", nullable = false, length = 3)
    @NotNull
    private String currency = "EUR";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull
    private PostStatus status = PostStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", length = 20, nullable = true)
    private PostCondition condition;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "post_images",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "image_url", columnDefinition = "TEXT")
    @OrderColumn(name = "position")
    @Size(max = 10, message = "Cannot have more than 10 images")
    private List<String> images = new ArrayList<>();

    @Nullable
    public String getFirstImageUrl() {
        return (images != null && !images.isEmpty()) ? images.get(0) : null;
    }

    public void incrementViewCount() {
        if (viewCount == null) viewCount = 0L;
        viewCount++;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", category=" + category +
                ", title='" + title + '\'' +
                ", marketActor=" + (marketActor != null ? marketActor.getId() : "null") +
                ", status=" + status +
                ", isActive=" + isActive +
                ", imagesCount=" + (images != null ? images.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}