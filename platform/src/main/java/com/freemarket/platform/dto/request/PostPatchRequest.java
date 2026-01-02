package com.freemarket.platform.dto.request;

import com.freemarket.platform.entity.PostCategory;
import com.freemarket.platform.entity.PostCondition;
import com.freemarket.platform.entity.PostStatus;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public record PostPatchRequest(
        PostCategory category,

        @Size(min = 5, max = 200)
        String title,

        @Size(min = 10, max = 5000)
        String description,

        @Size(max = 100)
        String location,

        @Size(max = 100)
        String priceInfo,

        @Size(max = 5000)
        String contactInfo,

        @Size(max = 5)
        String currency,

        PostCondition condition,

        PostStatus status,

        Set<@Size(max = 50) String> tags,

        @Size(max = 10)
        List<@Size(max = 2048) String> images,

        @Size(max = 2048)
        String thumbnailUrl
) {
}
