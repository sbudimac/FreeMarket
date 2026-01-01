package com.freemarket.platform.security;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public final class CurrentUserRetreiver {

    private CurrentUserRetreiver() {}

    /**
     * Extracts the current user's UUID from Authentication.
     */
    public static UUID getCurrentUserID(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("No user is currently authenticated.");
        }

        return UUID.fromString(authentication.getName());
    }
}
