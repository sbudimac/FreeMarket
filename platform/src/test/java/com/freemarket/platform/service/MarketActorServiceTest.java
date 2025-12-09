package com.freemarket.platform.service;

import com.freemarket.platform.dto.request.LoginRequest;
import com.freemarket.platform.dto.request.RegisterRequest;
import com.freemarket.platform.dto.response.MarketActorResponse;
import com.freemarket.platform.entity.MarketActor;
import com.freemarket.platform.repository.MarketActorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketActorServiceTest {

    @Mock
    private MarketActorRepository marketActorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MarketActorService marketActorService;

    private MarketActor testMarketActor;
    private UUID testId;
    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testMarketActor = new MarketActor(
                "testuser",
                "test@example.com",
                "hashedPassword123",
                "Phone: 123-4567"
        );
        testMarketActor.setId(testId);
        testMarketActor.setIsVerified(false);
        testMarketActor.setCreatedAt(LocalDateTime.now());

        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUsername("newuser");
        validRegisterRequest.setEmail("new@example.com");
        validRegisterRequest.setPassword("Password123");
        validRegisterRequest.setContactInfo("Contact info");

        validLoginRequest = new LoginRequest("testuser", "password123");
    }

    // ===== REGISTRATION TESTS =====
//
//    @Test
//    void registerMarketActor_WithValidRequest_ShouldSuccess() {
//        // Arrange
//        when(marketActorRepository.existsByUsername(anyString())).thenReturn(false);
//        when(marketActorRepository.existsByEmail(anyString())).thenReturn(false);
//        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
//        when(marketActorRepository.save(any(MarketActor.class))).thenReturn(testMarketActor);
//
//        // Act
//        MarketActor result = marketActorService.registerMarketActor(validRegisterRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("testuser", result.getUsername());
//        assertEquals("test@example.com", result.getEmail());
//        verify(marketActorRepository).save(any(MarketActor.class));
//        verify(passwordEncoder).encode("Password123");
//    }
//
//    @Test
//    void registerMarketActor_WithExistingUsername_ShouldThrowException() {
//        // Arrange
//        when(marketActorRepository.existsByUsername(anyString())).thenReturn(true);
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> marketActorService.registerMarketActor(validRegisterRequest));
//
//        assertTrue(exception.getMessage().contains("Username already exists"));
//        verify(marketActorRepository, never()).save(any(MarketActor.class));
//    }
//
//    @Test
//    void registerMarketActor_WithExistingEmail_ShouldThrowException() {
//        // Arrange
//        when(marketActorRepository.existsByUsername(anyString())).thenReturn(false);
//        when(marketActorRepository.existsByEmail(anyString())).thenReturn(true);
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> marketActorService.registerMarketActor(validRegisterRequest));
//
//        assertTrue(exception.getMessage().contains("Email already exists"));
//        verify(marketActorRepository, never()).save(any(MarketActor.class));
//    }
//
//    @Test
//    void registerMarketActor_WithInvalidEmail_ShouldThrowException() {
//        // Arrange
//        RegisterRequest invalidRequest = new RegisterRequest();
//        invalidRequest.setUsername("validuser");
//        invalidRequest.setEmail("invalid-email");
//        invalidRequest.setPassword("Password123");
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> marketActorService.registerMarketActor(invalidRequest));
//
//        assertTrue(exception.getMessage().contains("Invalid email format"));
//    }
//
//    // ===== AUTHENTICATION TESTS =====
//
//    @Test
//    void authenticate_WithValidCredentials_ShouldReturnTrue() {
//        // Arrange
//        when(marketActorRepository.findByUsername("testuser")).thenReturn(Optional.of(testMarketActor));
//        when(passwordEncoder.matches("password123", "hashedPassword123")).thenReturn(true);
//
//        // Act
//        boolean result = marketActorService.authenticate(validLoginRequest);
//
//        // Assert
//        assertTrue(result);
//    }
//
//    @Test
//    void authenticate_WithInvalidPassword_ShouldReturnFalse() {
//        // Arrange
//        when(marketActorRepository.findByUsername("testuser")).thenReturn(Optional.of(testMarketActor));
//        when(passwordEncoder.matches("wrongpassword", "hashedPassword123")).thenReturn(false);
//
//        // Act
//        LoginRequest wrongPasswordRequest = new LoginRequest("testuser", "wrongpassword");
//        boolean result = marketActorService.authenticate(wrongPasswordRequest);
//
//        // Assert
//        assertFalse(result);
//    }
//
//    @Test
//    void authenticate_WithNonExistentUser_ShouldReturnFalse() {
//        // Arrange
//        when(marketActorRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
//
//        // Act
//        LoginRequest nonExistentRequest = new LoginRequest("nonexistent", "password");
//        boolean result = marketActorService.authenticate(nonExistentRequest);
//
//        // Assert
//        assertFalse(result);
//    }
//
//    @Test
//    void authenticateAndGetMarketActor_WithValidCredentials_ShouldReturnMarketActor() {
//        // Arrange
//        when(marketActorRepository.findByUsername("testuser")).thenReturn(Optional.of(testMarketActor));
//        when(passwordEncoder.matches("password123", "hashedPassword123")).thenReturn(true);
//
//        // Act
//        Optional<MarketActor> result = marketActorService.authenticateAndGetMarketActor(validLoginRequest);
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals("testuser", result.get().getUsername());
//    }

    // ===== READ OPERATION TESTS =====

    @Test
    void findById_WithExistingId_ShouldReturnMarketActor() {
        // Arrange
        when(marketActorRepository.findById(testId)).thenReturn(Optional.of(testMarketActor));

        // Act
        Optional<MarketActor> result = marketActorService.findById(testId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());
    }

    @Test
    void findById_WithNonExistentId_ShouldReturnEmpty() {
        // Arrange
        when(marketActorRepository.findById(testId)).thenReturn(Optional.empty());

        // Act
        Optional<MarketActor> result = marketActorService.findById(testId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByUsername_WithExistingUsername_ShouldReturnMarketActor() {
        // Arrange
        when(marketActorRepository.findByUsername("testuser")).thenReturn(Optional.of(testMarketActor));

        // Act
        Optional<MarketActor> result = marketActorService.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findAll_ShouldReturnAllMarketActors() {
        // Arrange
        List<MarketActor> actors = List.of(testMarketActor);
        when(marketActorRepository.findAll()).thenReturn(actors);

        // Act
        List<MarketActor> result = marketActorService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(testMarketActor, result.get(0));
    }

    // ===== UPDATE OPERATION TESTS =====

    @Test
    void updateContactInfo_WithExistingId_ShouldUpdateContactInfo() {
        // Arrange
        String newContactInfo = "New contact info";
        when(marketActorRepository.findById(testId)).thenReturn(Optional.of(testMarketActor));
        when(marketActorRepository.save(any(MarketActor.class))).thenReturn(testMarketActor);

        // Act
        MarketActor result = marketActorService.updateContactInfo(testId, newContactInfo);

        // Assert
        assertNotNull(result);
        verify(marketActorRepository).save(testMarketActor);
    }

    @Test
    void updateContactInfo_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(marketActorRepository.findById(testId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> marketActorService.updateContactInfo(testId, "new contact"));

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void verifyMarketActor_ShouldSetVerifiedToTrue() {
        // Arrange
        testMarketActor.setIsVerified(false);
        when(marketActorRepository.findById(testId)).thenReturn(Optional.of(testMarketActor));
        when(marketActorRepository.save(any(MarketActor.class))).thenReturn(testMarketActor);

        // Act
        MarketActor result = marketActorService.verifyMarketActor(testId);

        // Assert
        assertTrue(result.getIsVerified());
        verify(marketActorRepository).save(testMarketActor);
    }

    @Test
    void changePassword_WithExistingId_ShouldUpdatePassword() {
        // Arrange
        String newPassword = "NewPassword123";
        when(marketActorRepository.findById(testId)).thenReturn(Optional.of(testMarketActor));
        when(passwordEncoder.encode(newPassword)).thenReturn("newHashedPassword");
        when(marketActorRepository.save(any(MarketActor.class))).thenReturn(testMarketActor);

        // Act
        MarketActor result = marketActorService.changePassword(testId, newPassword);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder).encode(newPassword);
        verify(marketActorRepository).save(testMarketActor);
    }

    // ===== VALIDATION TESTS =====

    @Test
    void existsByUsername_WithExistingUsername_ShouldReturnTrue() {
        // Arrange
        when(marketActorRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act
        boolean result = marketActorService.existsByUsername("existinguser");

        // Assert
        assertTrue(result);
    }

    @Test
    void isUsernameAvailable_WithAvailableUsername_ShouldReturnTrue() {
        // Arrange
        when(marketActorRepository.existsByUsername("newuser")).thenReturn(false);

        // Act
        boolean result = marketActorService.isUsernameAvailable("newuser");

        // Assert
        assertTrue(result);
    }

    @Test
    void isEmailAvailable_WithAvailableEmail_ShouldReturnTrue() {
        // Arrange
        when(marketActorRepository.existsByEmail("new@example.com")).thenReturn(false);

        // Act
        boolean result = marketActorService.isEmailAvailable("new@example.com");

        // Assert
        assertTrue(result);
    }

    // ===== BUSINESS LOGIC TESTS =====

    @Test
    void getTotalMarketActorsCount_ShouldReturnCount() {
        // Arrange
        when(marketActorRepository.count()).thenReturn(10L);

        // Act
        long result = marketActorService.getTotalMarketActorsCount();

        // Assert
        assertEquals(10L, result);
    }

    @Test
    void getVerifiedMarketActorsCount_ShouldReturnVerifiedCount() {
        // Arrange
        when(marketActorRepository.countByIsVerified(true)).thenReturn(5L);

        // Act
        long result = marketActorService.getVerifiedMarketActorsCount();

        // Assert
        assertEquals(5L, result);
    }

    // ===== DTO CONVERSION TESTS =====

    @Test
    void convertToMarketActorResponse_ShouldConvertCorrectly() {
        // Act
        MarketActorResponse response = marketActorService.convertToMarketActorResponse(testMarketActor);

        // Assert
        assertNotNull(response);
        assertEquals(testMarketActor.getId(), response.getId());
        assertEquals(testMarketActor.getUsername(), response.getUsername());
        assertEquals(testMarketActor.getEmail(), response.getEmail());
        assertEquals(testMarketActor.getContactInfo(), response.getContactInfo());
        assertEquals(testMarketActor.getIsVerified(), response.getIsVerified());
        assertEquals(testMarketActor.getCreatedAt(), response.getCreatedAt());
    }

    @Test
    void convertToMarketActorResponseList_ShouldConvertList() {
        // Arrange
        List<MarketActor> actors = List.of(testMarketActor);

        // Act
        List<MarketActorResponse> responses = marketActorService.convertToMarketActorResponseList(actors);

        // Assert
        assertEquals(1, responses.size());
        assertEquals(testMarketActor.getId(), responses.get(0).getId());
    }

    @Test
    void findMarketActorResponseById_WithExistingId_ShouldReturnResponse() {
        // Arrange
        when(marketActorRepository.findById(testId)).thenReturn(Optional.of(testMarketActor));

        // Act
        Optional<MarketActorResponse> result = marketActorService.findMarketActorResponseById(testId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());
    }

    // ===== BULK OPERATION TESTS =====

    @Test
    void bulkVerifyMarketActors_ShouldVerifyAll() {
        // Arrange
        List<UUID> ids = List.of(testId);
        when(marketActorRepository.findAllById(ids)).thenReturn(List.of(testMarketActor));
        when(marketActorRepository.saveAll(any())).thenReturn(List.of(testMarketActor));

        // Act
        List<MarketActor> result = marketActorService.bulkVerifyMarketActors(ids);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsVerified());
        verify(marketActorRepository).saveAll(any());
    }

    // ===== EDGE CASE TESTS =====

    @Test
    void createMarketActor_WithNullPassword_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> marketActorService.createMarketActor("user", "email@test.com", null, "contact"));

        assertTrue(exception.getMessage().contains("Password cannot be null or empty"));
    }

    @Test
    void createMarketActor_WithShortUsername_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> marketActorService.createMarketActor("ab", "email@test.com", "password", "contact"));

        assertTrue(exception.getMessage().contains("Username must be between 3 and 50 characters"));
    }

    @Test
    void deleteMarketActor_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(marketActorRepository.existsById(testId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> marketActorService.deleteMarketActor(testId));

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void deleteMarketActor_WithExistingId_ShouldDelete() {
        // Arrange
        when(marketActorRepository.existsById(testId)).thenReturn(true);
        doNothing().when(marketActorRepository).deleteById(testId);

        // Act
        marketActorService.deleteMarketActor(testId);

        // Assert
        verify(marketActorRepository).deleteById(testId);
    }
}