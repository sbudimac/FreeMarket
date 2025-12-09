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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthController {

    private final MarketActorService marketActorService;
    private final AuthService authService;

    @Autowired
    public AuthController(MarketActorService marketActorService, AuthService authService) {
        this.marketActorService = marketActorService;
        this.authService = authService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            MarketActor newUser = authService.registerMarketActor(request);
            MarketActorResponse response = marketActorService.convertToMarketActorResponse(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MarketActorController.ErrorResponse(e.getMessage()));
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
                .body(new MarketActorController.ErrorResponse("Invalid username or password"));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Boolean> authenticate(@Valid @RequestBody LoginRequest request) {
        boolean isAuthenticated = authService.authenticate(request);
        return ResponseEntity.ok(isAuthenticated);
    }
}
