package com.freemarket.platform.controller;

import com.freemarket.platform.dto.request.LoginRequest;
import com.freemarket.platform.dto.request.RegisterRequest;
import com.freemarket.platform.dto.response.MarketActorResponse;
import com.freemarket.platform.entity.MarketActor;
import com.freemarket.platform.service.AuthService;
import com.freemarket.platform.service.MarketActorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/market-actors")
@CrossOrigin(origins = "*") // Adjust for production
public class MarketActorController {

    private final MarketActorService marketActorService;
    private final AuthService authService;

    @Autowired
    public MarketActorController(MarketActorService marketActorService, AuthService authService) {
        this.marketActorService = marketActorService;
        this.authService = authService;
    }

    // ===== AUTHENTICATION ENDPOINTS =====

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            MarketActor newUser = authService.registerMarketActor(request);
            MarketActorResponse response = marketActorService.convertToMarketActorResponse(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<MarketActor> user = authService.authenticateAndGetMarketActor(request);
        if (user.isPresent()) {
            MarketActorResponse response = marketActorService.convertToMarketActorResponse(user.get());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid username or password"));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Boolean> authenticate(@Valid @RequestBody LoginRequest request) {
        boolean isAuthenticated = authService.authenticate(request);
        return ResponseEntity.ok(isAuthenticated);
    }

    // ===== PUBLIC READ ENDPOINTS =====

    @GetMapping
    public ResponseEntity<List<MarketActorResponse>> getAllMarketActors() {
        List<MarketActor> actors = marketActorService.findAll();
        List<MarketActorResponse> responses = marketActorService.convertToMarketActorResponseList(actors);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMarketActorById(@PathVariable UUID id) {
        Optional<MarketActorResponse> userResponse = marketActorService.findMarketActorResponseById(id);
        return userResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getMarketActorByUsername(@PathVariable String username) {
        Optional<MarketActorResponse> userResponse = marketActorService.findMarketActorResponseByUsername(username);
        return userResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<MarketActorResponse>> searchMarketActors(@RequestParam String username) {
        List<MarketActor> actors = marketActorService.searchByUsername(username);
        List<MarketActorResponse> responses = marketActorService.convertToMarketActorResponseList(actors);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/verified")
    public ResponseEntity<List<MarketActorResponse>> getVerifiedMarketActors() {
        List<MarketActor> actors = marketActorService.findVerifiedActors();
        List<MarketActorResponse> responses = marketActorService.convertToMarketActorResponseList(actors);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/unverified")
    public ResponseEntity<List<MarketActorResponse>> getUnverifiedMarketActors() {
        List<MarketActor> actors = marketActorService.findUnverifiedActors();
        List<MarketActorResponse> responses = marketActorService.convertToMarketActorResponseList(actors);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/with-active-posts")
    public ResponseEntity<List<MarketActorResponse>> getActorsWithActivePosts() {
        List<MarketActor> actors = marketActorService.findActorsWithActivePosts();
        List<MarketActorResponse> responses = marketActorService.convertToMarketActorResponseList(actors);
        return ResponseEntity.ok(responses);
    }

    // ===== VALIDATION ENDPOINTS =====

    @GetMapping("/check-username")
    public ResponseEntity<AvailabilityResponse> checkUsernameAvailability(@RequestParam String username) {
        boolean isAvailable = marketActorService.isUsernameAvailable(username);
        return ResponseEntity.ok(new AvailabilityResponse(isAvailable,
                isAvailable ? "Username is available" : "Username already taken"));
    }

    @GetMapping("/check-email")
    public ResponseEntity<AvailabilityResponse> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = marketActorService.isEmailAvailable(email);
        return ResponseEntity.ok(new AvailabilityResponse(isAvailable,
                isAvailable ? "Email is available" : "Email already registered"));
    }

    // ===== PROTECTED UPDATE ENDPOINTS =====

    @PutMapping("/{id}/contact-info")
    public ResponseEntity<?> updateContactInfo(@PathVariable UUID id, @RequestBody ContactInfoRequest request) {
        try {
            MarketActor updatedActor = marketActorService.updateContactInfo(id, request.getContactInfo());
            MarketActorResponse response = marketActorService.convertToMarketActorResponse(updatedActor);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<?> updateEmail(@PathVariable UUID id, @Valid @RequestBody EmailUpdateRequest request) {
        try {
            MarketActor updatedActor = marketActorService.updateEmail(id, request.getEmail());
            MarketActorResponse response = marketActorService.convertToMarketActorResponse(updatedActor);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable UUID id, @Valid @RequestBody PasswordChangeRequest request) {
        try {
            MarketActor updatedActor = marketActorService.changePassword(id, request.getNewPassword());
            return ResponseEntity.ok(new MessageResponse("Password updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifyMarketActor(@PathVariable UUID id) {
        try {
            MarketActor verifiedActor = marketActorService.verifyMarketActor(id);
            MarketActorResponse response = marketActorService.convertToMarketActorResponse(verifiedActor);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===== ADMIN ENDPOINTS =====

    @GetMapping("/stats/count")
    public ResponseEntity<MarketActorStats> getMarketActorStats() {
        long total = marketActorService.getTotalMarketActorsCount();
        long verified = marketActorService.getVerifiedMarketActorsCount();
        long unverified = marketActorService.getUnverifiedMarketActorsCount();

        MarketActorStats stats = new MarketActorStats(total, verified, unverified);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/bulk-verify")
    public ResponseEntity<?> bulkVerifyMarketActors(@RequestBody List<UUID> ids) {
        try {
            List<MarketActor> verifiedActors = marketActorService.bulkVerifyMarketActors(ids);
            List<MarketActorResponse> responses = marketActorService.convertToMarketActorResponseList(verifiedActors);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error during bulk verification"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMarketActor(@PathVariable UUID id) {
        try {
            marketActorService.deleteMarketActor(id);
            return ResponseEntity.ok(new MessageResponse("Market actor deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===== SUPPORTING DTO CLASSES =====

    // Inner DTO classes for request/response
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class AvailabilityResponse {
        private boolean available;
        private String message;

        public AvailabilityResponse(boolean available, String message) {
            this.available = available;
            this.message = message;
        }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ContactInfoRequest {
        private String contactInfo;

        public String getContactInfo() { return contactInfo; }
        public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    }

    public static class EmailUpdateRequest {
        @jakarta.validation.constraints.Email
        private String email;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class PasswordChangeRequest {
        @jakarta.validation.constraints.Size(min = 6)
        private String newPassword;

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class MarketActorStats {
        private long totalActors;
        private long verifiedActors;
        private long unverifiedActors;

        public MarketActorStats(long totalActors, long verifiedActors, long unverifiedActors) {
            this.totalActors = totalActors;
            this.verifiedActors = verifiedActors;
            this.unverifiedActors = unverifiedActors;
        }

        // Getters
        public long getTotalActors() { return totalActors; }
        public long getVerifiedActors() { return verifiedActors; }
        public long getUnverifiedActors() { return unverifiedActors; }
    }
}