package com.freemarket.platform.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;

import java.util.Set;

@Getter
@Setter
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
    private Set<@Pattern(
            regexp = "^(http|https)://.*\\.(jpg|jpeg|png|gif|webp|bmp)$",
            message = "Each image URL must be a valid URL ending with jpg, jpeg, png, gif, webp, or bmp"
    ) String> images;

    // Constructors
    public UpdatePostRequest() {}

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
