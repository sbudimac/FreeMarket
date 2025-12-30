package com.freemarket.platform.service;

import com.freemarket.platform.dto.request.RegisterRequest;
import com.freemarket.platform.entity.MarketActor;
import com.freemarket.platform.repository.MarketActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final MarketActorRepository marketActorRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(MarketActorRepository marketActorRepository, PasswordEncoder passwordEncoder) {
        this.marketActorRepository = marketActorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public MarketActor registerMarketActor(RegisterRequest registerRequest) throws IllegalArgumentException {
        validateNewMarketActor(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());

        MarketActor marketActor = new MarketActor();
        marketActor.setUsername(registerRequest.getUsername());
        marketActor.setEmail(registerRequest.getEmail());
        marketActor.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        marketActor.setContactInfo(registerRequest.getContactInfo());
        marketActor.setIsVerified(false);
        marketActor.getRoles().add("USER");

        return marketActorRepository.save(marketActor);
    }

    private void validateNewMarketActor(String username, String email, String password) throws IllegalArgumentException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (marketActorRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        if (marketActorRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }
}
