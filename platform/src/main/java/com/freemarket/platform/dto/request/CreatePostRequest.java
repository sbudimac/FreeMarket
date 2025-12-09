package com.freemarket.platform.dto.request;

import com.freemarket.platform.entity.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
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

    private LocalDateTime expiresAt; // Optional expiration

    // Constructors
    public CreatePostRequest() {}
}