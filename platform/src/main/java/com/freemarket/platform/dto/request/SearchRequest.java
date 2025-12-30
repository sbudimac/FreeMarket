package com.freemarket.platform.dto.request;

import com.freemarket.platform.entity.PostType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SearchRequest {
    @NotNull(message = "Post type is required")
    private PostType type;

    @Size(max = 100, message = "Location must be less than 100 characters")
    private String location;

    @Size(max = 10, message = "Maximum 10 tags allowed")
    private List<@Size(max = 50, message = "Each tag must be less than 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_\\- ]+$", message = "Invalid tag format")
            String> tags;

    @Size(max = 200, message = "Search term too long")
    private String search;

    @Min(value = 0, message = "Page must be positive")
    private Integer page = 0;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size cannot exceed 100")
    private Integer size = 20;

    private SortField sortBy = SortField.CREATED_AT;
    private SortDirection sortDirection = SortDirection.DESC;

    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private Boolean activeOnly = true;

    // Enums for better type safety
    public enum SortField {
        CREATED_AT, TITLE, UPDATED_AT
    }

    public enum SortDirection {
        ASC, DESC
    }

    // Constructors
    public SearchRequest() {}

    // Builder pattern for fluent API (optional but nice)
    public static SearchRequestBuilder builder() {
        return new SearchRequestBuilder();
    }

    // Builder class
    public static class SearchRequestBuilder {
        private SearchRequest request = new SearchRequest();

        public SearchRequestBuilder type(PostType type) {
            request.setType(type);
            return this;
        }

        public SearchRequestBuilder location(String location) {
            request.setLocation(location);
            return this;
        }

        public SearchRequestBuilder search(String search) {
            request.setSearch(search);
            return this;
        }

        public SearchRequestBuilder page(Integer page) {
            request.setPage(page);
            return this;
        }

        public SearchRequestBuilder size(Integer size) {
            request.setSize(size);
            return this;
        }

        public SearchRequest build() {
            return request;
        }
    }
}