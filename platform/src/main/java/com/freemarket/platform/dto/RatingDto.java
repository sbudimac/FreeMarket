package com.freemarket.platform.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RatingDto(
        UUID id,
        int score,
        String comment,
        LocalDateTime createdAt,
        UserPublicDto rater
) {
}
