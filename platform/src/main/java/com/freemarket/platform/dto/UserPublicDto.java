package com.freemarket.platform.dto;

import java.util.UUID;

public record UserPublicDto(
        UUID id,
        String username
) {
}
