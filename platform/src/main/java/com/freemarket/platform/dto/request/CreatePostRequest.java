package com.freemarket.platform.dto.request;

import com.freemarket.platform.entity.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.Set;

public class CreatePostRequest {
    @NotNull(message = "Post type is required")
    private PostType type;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    @Size(max = 100, message = "Price info cannot exceed 100 characters")
    private String priceInfo;

    private String contactInfo;

    @Size(max = 20, message = "Cannot have more than 20 tags")
    private Set<String> tags;

    @Size(max = 10, message = "Cannot have more than 10 images")
    private Set<@Pattern(
            regexp = "^(http|https)://.*\\.(jpg|jpeg|png|gif|webp|bmp)$",
            message = "Each image URL must be a valid URL ending with jpg, jpeg, png, gif, webp, or bmp"
    ) String> images;

    private LocalDateTime expiresAt; // Optional expiration

    // Constructors
    public CreatePostRequest() {}

    // Getters and Setters
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

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    // NEW: Images getter and setter
    public Set<String> getImages() { return images; }
    public void setImages(Set<String> images) { this.images = images; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}