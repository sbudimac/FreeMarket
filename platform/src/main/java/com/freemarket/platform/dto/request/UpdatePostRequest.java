package com.freemarket.platform.dto.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.springframework.lang.Nullable;

import java.util.Set;

public class UpdatePostRequest {
    @Size(max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    @Size(max = 100)
    private String location;

    @Size(max = 100)
    private String priceInfo;

    private String contactInfo;

    private Set<String> tags;

    private Boolean isActive;

    @Size(max = 10, message = "Cannot have more than 10 images")
    @Nullable
    private Set<@Pattern(
            regexp = "^(http|https)://.*\\.(jpg|jpeg|png|gif|webp|bmp)$",
            message = "Each image URL must be a valid URL ending with jpg, jpeg, png, gif, webp, or bmp"
    ) String> images;

    // Constructors
    public UpdatePostRequest() {}

    // Getters and Setters
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

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    @Nullable
    public Set<String> getImages() { return images; }
    public void setImages(@Nullable Set<String> images) { this.images = images; }

    public boolean hasImages() {
        return images != null;
    }

    public boolean hasUpdates() {
        return title != null || description != null || location != null ||
                priceInfo != null || contactInfo != null || tags != null ||
                isActive != null || images != null;
    }

    @Override
    public String toString() {
        return "UpdatePostRequest{" +
                "title='" + title + '\'' +
                ", descriptionLength=" + (description != null ? description.length() : 0) +
                ", location='" + location + '\'' +
                ", priceInfo='" + priceInfo + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", tags=" + tags +
                ", imagesCount=" + (images != null ? images.size() : 0) + // NEW: Include image count
                ", isActive=" + isActive +
                '}';
    }
}