package com.freemarket.platform.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record RatingRequest(
        @Min(1)
        @Max(5)
        Integer score,

        @Size(max = 1000)
        String comment
) {
}
