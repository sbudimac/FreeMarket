package com.freemarket.platform.dto.response;

import lombok.Getter;

import java.util.Set;

@Getter
public class LoginResponse {
    private final String accessToken;
    private final String tokenType = "Bearer";
    private final String username;
    private final Set<String> roles;

    public LoginResponse(String accessToken, String username, Set<String> roles) {
        this.accessToken = accessToken;
        this.username = username;
        this.roles = roles;
    }
}
