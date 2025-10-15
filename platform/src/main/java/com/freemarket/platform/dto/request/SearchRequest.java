package com.freemarket.platform.dto.request;

import com.freemarket.platform.entity.PostType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    // Getters and Setters (your existing ones plus new fields)
    public PostType getType() { return type; }
    public void setType(PostType type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public SortField getSortBy() { return sortBy; }
    public void setSortBy(SortField sortBy) { this.sortBy = sortBy; }

    public SortDirection getSortDirection() { return sortDirection; }
    public void setSortDirection(SortDirection sortDirection) { this.sortDirection = sortDirection; }

    public LocalDateTime getCreatedAfter() { return createdAfter; }
    public void setCreatedAfter(LocalDateTime createdAfter) { this.createdAfter = createdAfter; }

    public LocalDateTime getCreatedBefore() { return createdBefore; }
    public void setCreatedBefore(LocalDateTime createdBefore) { this.createdBefore = createdBefore; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    public Boolean getActiveOnly() { return activeOnly; }
    public void setActiveOnly(Boolean activeOnly) { this.activeOnly = activeOnly; }

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