package com.freemarket.platform.dto;

import com.freemarket.platform.entity.PostCategory;
import com.freemarket.platform.entity.PostCondition;
import com.freemarket.platform.entity.PostStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record PostDetailsDto(
        UUID id,
        PostCategory category,
        String title,
        String description,
        String location,
        String priceInfo,
        String currency,
        PostCondition condition,
        PostStatus status,
        String thumbnailUrl,
        List<String> images,
        Set<String> tags,
        long viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UserPublicDto owner
) {
}
