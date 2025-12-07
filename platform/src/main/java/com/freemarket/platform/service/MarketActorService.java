package com.freemarket.platform.service;

import com.freemarket.platform.dto.request.LoginRequest;
import com.freemarket.platform.dto.request.RegisterRequest;
import com.freemarket.platform.dto.response.MarketActorResponse;
import com.freemarket.platform.entity.MarketActor;
import com.freemarket.platform.repository.MarketActorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarketActorService {
    private final MarketActorRepository marketActorRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MarketActorService(MarketActorRepository marketActorRepository, PasswordEncoder passwordEncoder) {
        this.marketActorRepository = marketActorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // READ operations
    public Optional<MarketActor> findById(UUID id) {
        return marketActorRepository.findById(id);
    }

    public Optional<MarketActor> findByUsername(String username) {
        return marketActorRepository.findByUsername(username);
    }

    public Optional<MarketActor> findByEmail(String email) {
        return marketActorRepository.findByEmail(email);
    }

    public List<MarketActor> findAll() {
        return marketActorRepository.findAll();
    }

    public List<MarketActor> findVerifiedActors() {
        return marketActorRepository.findByIsVerifiedTrue();
    }

    public List<MarketActor> findUnverifiedActors() {
        return marketActorRepository.findByIsVerifiedFalse();
    }

    public List<MarketActor> searchByUsername(String searchTerm) {
        return marketActorRepository.findByUsernameContainingIgnoreCase(searchTerm);
    }

    public List<MarketActor> findActorsWithActivePosts() {
        return marketActorRepository.findWithActivePosts();
    }

    // UPDATE operations
    public MarketActor updateContactInfo(UUID id, String newContactInfo) {
        MarketActor marketActor = marketActorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MarketActor not found with id: " + id));

        marketActor.setContactInfo(newContactInfo);
        return marketActorRepository.save(marketActor);
    }

    public MarketActor updateEmail(UUID id, String newEmail) {
        if (marketActorRepository.existsByEmail(newEmail)) {
            throw new RuntimeException("Email already exists: " + newEmail);
        }

        MarketActor marketActor = marketActorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MarketActor not found with id: " + id));

        marketActor.setEmail(newEmail);
        return marketActorRepository.save(marketActor);
    }

    public MarketActor verifyMarketActor(UUID id) {
        MarketActor marketActor = marketActorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MarketActor not found with id: " + id));

        marketActor.verify();
        return marketActorRepository.save(marketActor);
    }

    public MarketActor changePassword(UUID id, String newPlainPassword) {
        MarketActor marketActor = marketActorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MarketActor not found with id: " + id));

        marketActor.setPasswordHash(passwordEncoder.encode(newPlainPassword));
        return marketActorRepository.save(marketActor);
    }

    // DELETE operations
    public void deleteMarketActor(UUID id) {
        if (!marketActorRepository.existsById(id)) {
            throw new RuntimeException("MarketActor not found with id: " + id);
        }
        marketActorRepository.deleteById(id);
    }

    // VALIDATION operations
    public boolean existsByUsername(String username) {
        return marketActorRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return marketActorRepository.existsByEmail(email);
    }

    public boolean isUsernameAvailable(String username) {
        return !marketActorRepository.existsByUsername(username);
    }

    public boolean isEmailAvailable(String email) {
        return !marketActorRepository.existsByEmail(email);
    }

    // BUSINESS LOGIC operations
    public long getTotalMarketActorsCount() {
        return marketActorRepository.count();
    }

    public long getVerifiedMarketActorsCount() {
        return marketActorRepository.countByIsVerified(true);
    }

    public long getUnverifiedMarketActorsCount() {
        return marketActorRepository.countByIsVerified(false);
    }

    // DTO CONVERSION operations
    public MarketActorResponse convertToMarketActorResponse(MarketActor marketActor) {
        return MarketActorResponse.builder()
                .id(marketActor.getId())
                .username(marketActor.getUsername())
                .email(marketActor.getEmail())
                .contactInfo(marketActor.getContactInfo())
                .isVerified(marketActor.getIsVerified())
                .createdAt(marketActor.getCreatedAt())
                .build();
    }

    public List<MarketActorResponse> convertToMarketActorResponseList(List<MarketActor> marketActors) {
        return marketActors.stream()
                .map(this::convertToMarketActorResponse)
                .collect(Collectors.toList());
    }

    public Optional<MarketActorResponse> findMarketActorResponseById(UUID id) {
        return marketActorRepository.findById(id)
                .map(this::convertToMarketActorResponse);
    }

    public Optional<MarketActorResponse> findMarketActorResponseByUsername(String username) {
        return marketActorRepository.findByUsername(username)
                .map(this::convertToMarketActorResponse);
    }

    // Bulk operations
    public List<MarketActor> bulkVerifyMarketActors(List<UUID> ids) {
        List<MarketActor> marketActors = marketActorRepository.findAllById(ids);

        for (MarketActor marketActor : marketActors) {
            marketActor.verify();
        }

        return marketActorRepository.saveAll(marketActors);
    }

    // Search by location in posts
    public List<MarketActor> findActorsByPostLocation(String location) {
        return marketActorRepository.findByPostLocationContaining(location);
    }

    // Profile completeness check (for future use)
    public boolean isProfileComplete(UUID id) {
        MarketActor marketActor = marketActorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MarketActor not found with id: " + id));

        return marketActor.getContactInfo() != null &&
                !marketActor.getContactInfo().trim().isEmpty() &&
                marketActor.getIsVerified();
    }
}