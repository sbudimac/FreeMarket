package com.freemarket.platform.dto;

import com.freemarket.platform.entity.PostCategory;
import com.freemarket.platform.entity.PostStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostListItemDto(
        UUID id,
        String title,
        PostCategory category,
        PostStatus status,
        String location,
        String priceInfo,
        String currency,
        String thumbnailUrl,
        LocalDateTime createdAt,
        UserPublicDto owner
) {
}
