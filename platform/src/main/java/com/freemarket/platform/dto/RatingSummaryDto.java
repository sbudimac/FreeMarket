package com.freemarket.platform.dto;

public record RatingSummaryDto(
        double averageScore,
        long count
) {
}
