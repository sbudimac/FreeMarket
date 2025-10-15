package com.freemarket.platform.dto.request;

import jakarta.validation.constraints.Size;

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

    @Override
    public String toString() {
        return "UpdatePostRequest{" +
                "title='" + title + '\'' +
                ", descriptionLength=" + (description != null ? description.length() : 0) +
                ", location='" + location + '\'' +
                ", priceInfo='" + priceInfo + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", tags=" + tags +
                ", isActive=" + isActive +
                '}';
    }
}
