package com.freemarket.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freemarket.platform.config.TestSecurityConfig;
import com.freemarket.platform.dto.request.LoginRequest;
import com.freemarket.platform.dto.request.RegisterRequest;
import com.freemarket.platform.dto.response.MarketActorResponse;
import com.freemarket.platform.entity.MarketActor;
import com.freemarket.platform.service.MarketActorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MarketActorController.class)
@Import(TestSecurityConfig.class)
class MarketActorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MarketActorService marketActorService;

    private MarketActor testMarketActor;
    private MarketActorResponse testMarketActorResponse;
    private UUID testId;
    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        testMarketActor = new MarketActor(
                "testuser",
                "test@example.com",
                "hashedPassword",
                "Phone: 123-4567"
        );
        testMarketActor.setId(testId);
        testMarketActor.setIsVerified(true);
        testMarketActor.setCreatedAt(LocalDateTime.now());

        testMarketActorResponse = new MarketActorResponse(testId, "testUser", "test@example.com", "Phone: 123-4567", true, LocalDateTime.now());

        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUsername("newuser");
        validRegisterRequest.setEmail("new@example.com");
        validRegisterRequest.setPassword("Password123");
        validRegisterRequest.setContactInfo("Contact info");

        validLoginRequest = new LoginRequest("testuser", "password123");
    }

    // ===== AUTHENTICATION ENDPOINT TESTS =====

//    @Test
//    void register_WithValidRequest_ShouldReturnCreated() throws Exception {
//        // Arrange
//        when(marketActorService.registerMarketActor(Mockito.<RegisterRequest>any()))
//                .thenReturn(testMarketActor);
//        when(marketActorService.convertToMarketActorResponse(Mockito.<MarketActor>any()))
//                .thenReturn(testMarketActorResponse);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/market-actors/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.username", is("testuser")))
//                .andExpect(jsonPath("$.email", is("test@example.com")))
//                .andExpect(jsonPath("$.isVerified", is(true)));
//
//        verify(marketActorService).registerMarketActor(Mockito.<RegisterRequest>any());
//    }
//
//    @Test
//    void register_WithExistingUsername_ShouldReturnBadRequest() throws Exception {
//        // Arrange
//        when(marketActorService.registerMarketActor(Mockito.<RegisterRequest>any()))
//                .thenThrow(new RuntimeException("Username already exists: newuser"));
//
//        // Act & Assert
//        mockMvc.perform(post("/api/market-actors/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message", containsString("Username already exists")));
//    }
//
//    @Test
//    void register_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
//        // Arrange
//        RegisterRequest invalidRequest = new RegisterRequest();
//        invalidRequest.setUsername("ab"); // Too short
//        invalidRequest.setEmail("invalid-email");
//        invalidRequest.setPassword("short");
//
//        // Act & Assert
//        mockMvc.perform(post("/api/market-actors/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void login_WithValidCredentials_ShouldReturnOk() throws Exception {
//        // Arrange
//        when(marketActorService.authenticateAndGetMarketActor(Mockito.<LoginRequest>any()))
//                .thenReturn(Optional.of(testMarketActor));
//        when(marketActorService.convertToMarketActorResponse(Mockito.<MarketActor>any()))
//                .thenReturn(testMarketActorResponse);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/market-actors/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(validLoginRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username", is("testuser")));
//    }
//
//    @Test
//    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
//        // Arrange
//        when(marketActorService.authenticateAndGetMarketActor(Mockito.<LoginRequest>any()))
//                .thenReturn(Optional.empty());
//
//        // Act & Assert
//        mockMvc.perform(post("/api/market-actors/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(validLoginRequest)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.message", is("Invalid username or password")));
//    }
//
//    @Test
//    void authenticate_ShouldReturnBoolean() throws Exception {
//        // Arrange
//        when(marketActorService.authenticate(Mockito.<LoginRequest>any())).thenReturn(true);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/market-actors/authenticate")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(validLoginRequest)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("true"));
//    }

    // ===== READ ENDPOINT TESTS =====

    @Test
    void getAllMarketActors_ShouldReturnList() throws Exception {
        // Arrange
        List<MarketActor> actors = List.of(testMarketActor);
        List<MarketActorResponse> responses = List.of(testMarketActorResponse);

        when(marketActorService.findAll()).thenReturn(actors);
        when(marketActorService.convertToMarketActorResponseList(actors)).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/api/market-actors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("testuser")));
    }

    @Test
    void getMarketActorById_WithExistingId_ShouldReturnMarketActor() throws Exception {
        // Arrange
        when(marketActorService.findMarketActorResponseById(testId))
                .thenReturn(Optional.of(testMarketActorResponse));

        // Act & Assert
        mockMvc.perform(get("/api/market-actors/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testId.toString())))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    void getMarketActorById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(marketActorService.findMarketActorResponseById(testId))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/market-actors/{id}", testId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMarketActorByUsername_WithExistingUsername_ShouldReturnMarketActor() throws Exception {
        // Arrange
        when(marketActorService.findMarketActorResponseByUsername("testuser"))
                .thenReturn(Optional.of(testMarketActorResponse));

        // Act & Assert
        mockMvc.perform(get("/api/market-actors/username/{username}", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    void searchMarketActors_ShouldReturnFilteredList() throws Exception {
        // Arrange
        List<MarketActor> actors = List.of(testMarketActor);
        List<MarketActorResponse> responses = List.of(testMarketActorResponse);

        when(marketActorService.searchByUsername("test")).thenReturn(actors);
        when(marketActorService.convertToMarketActorResponseList(actors)).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/api/market-actors/search")
                        .param("username", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("testuser")));
    }

    @Test
    void getVerifiedMarketActors_ShouldReturnVerifiedOnly() throws Exception {
        // Arrange
        List<MarketActor> actors = List.of(testMarketActor);
        List<MarketActorResponse> responses = List.of(testMarketActorResponse);

        when(marketActorService.findVerifiedActors()).thenReturn(actors);
        when(marketActorService.convertToMarketActorResponseList(actors)).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/api/market-actors/verified"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isVerified", is(true)));
    }

    // ===== VALIDATION ENDPOINT TESTS =====

    @Test
    void checkUsernameAvailability_WithAvailableUsername_ShouldReturnTrue() throws Exception {
        // Arrange
        when(marketActorService.isUsernameAvailable("newuser")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/market-actors/check-username")
                        .param("username", "newuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.message", is("Username is available")));
    }

    @Test
    void checkEmailAvailability_WithTakenEmail_ShouldReturnFalse() throws Exception {
        // Arrange
        when(marketActorService.isEmailAvailable("taken@example.com")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/market-actors/check-email")
                        .param("email", "taken@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(false)))
                .andExpect(jsonPath("$.message", is("Email already registered")));
    }

    // ===== UPDATE ENDPOINT TESTS =====

    @Test
    void updateContactInfo_WithExistingId_ShouldReturnUpdated() throws Exception {
        // Arrange
        String newContactInfo = "New contact info";
        MarketActorController.ContactInfoRequest request = new MarketActorController.ContactInfoRequest();
        request.setContactInfo(newContactInfo);

        when(marketActorService.updateContactInfo(eq(testId), eq(newContactInfo)))
                .thenReturn(testMarketActor);
        when(marketActorService.convertToMarketActorResponse(testMarketActor))
                .thenReturn(testMarketActorResponse);

        // Act & Assert
        mockMvc.perform(put("/api/market-actors/{id}/contact-info", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    void updateContactInfo_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Arrange
        MarketActorController.ContactInfoRequest request = new MarketActorController.ContactInfoRequest();
        request.setContactInfo("new info");

        when(marketActorService.updateContactInfo(eq(testId), anyString()))
                .thenThrow(new RuntimeException("MarketActor not found"));

        // Act & Assert
        mockMvc.perform(put("/api/market-actors/{id}/contact-info", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateEmail_WithValidEmail_ShouldReturnUpdated() throws Exception {
        // Arrange
        MarketActorController.EmailUpdateRequest request = new MarketActorController.EmailUpdateRequest();
        request.setEmail("new@example.com");

        when(marketActorService.updateEmail(eq(testId), eq("new@example.com")))
                .thenReturn(testMarketActor);
        when(marketActorService.convertToMarketActorResponse(testMarketActor))
                .thenReturn(testMarketActorResponse);

        // Act & Assert
        mockMvc.perform(put("/api/market-actors/{id}/email", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    void updateEmail_WithTakenEmail_ShouldReturnBadRequest() throws Exception {
        // Arrange
        MarketActorController.EmailUpdateRequest request = new MarketActorController.EmailUpdateRequest();
        request.setEmail("taken@example.com");

        when(marketActorService.updateEmail(eq(testId), eq("taken@example.com")))
                .thenThrow(new RuntimeException("Email already exists"));

        // Act & Assert
        mockMvc.perform(put("/api/market-actors/{id}/email", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Email already exists")));
    }

    @Test
    void changePassword_WithValidRequest_ShouldReturnSuccess() throws Exception {
        // Arrange
        MarketActorController.PasswordChangeRequest request = new MarketActorController.PasswordChangeRequest();
        request.setNewPassword("NewPassword123");

        when(marketActorService.changePassword(eq(testId), eq("NewPassword123")))
                .thenReturn(testMarketActor);

        // Act & Assert
        mockMvc.perform(put("/api/market-actors/{id}/password", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Password updated successfully")));
    }

    @Test
    void verifyMarketActor_WithExistingId_ShouldReturnVerified() throws Exception {
        // Arrange
        when(marketActorService.verifyMarketActor(testId)).thenReturn(testMarketActor);
        when(marketActorService.convertToMarketActorResponse(testMarketActor))
                .thenReturn(testMarketActorResponse);

        // Act & Assert
        mockMvc.perform(put("/api/market-actors/{id}/verify", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isVerified", is(true)));
    }

    // ===== ADMIN ENDPOINT TESTS =====

    @Test
    void getMarketActorStats_ShouldReturnStats() throws Exception {
        // Arrange
        when(marketActorService.getTotalMarketActorsCount()).thenReturn(100L);
        when(marketActorService.getVerifiedMarketActorsCount()).thenReturn(75L);
        when(marketActorService.getUnverifiedMarketActorsCount()).thenReturn(25L);

        // Act & Assert
        mockMvc.perform(get("/api/market-actors/stats/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActors", is(100)))
                .andExpect(jsonPath("$.verifiedActors", is(75)))
                .andExpect(jsonPath("$.unverifiedActors", is(25)));
    }

    @Test
    void bulkVerifyMarketActors_ShouldReturnVerifiedList() throws Exception {
        // Arrange
        List<UUID> ids = List.of(testId);
        List<MarketActor> actors = List.of(testMarketActor);
        List<MarketActorResponse> responses = List.of(testMarketActorResponse);

        when(marketActorService.bulkVerifyMarketActors(ids)).thenReturn(actors);
        when(marketActorService.convertToMarketActorResponseList(actors)).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(post("/api/market-actors/bulk-verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isVerified", is(true)));
    }

    @Test
    void deleteMarketActor_WithExistingId_ShouldReturnSuccess() throws Exception {
        // Arrange
        doNothing().when(marketActorService).deleteMarketActor(testId);

        // Act & Assert
        mockMvc.perform(delete("/api/market-actors/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Market actor deleted successfully")));
    }

    @Test
    void deleteMarketActor_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("MarketActor not found"))
                .when(marketActorService).deleteMarketActor(testId);

        // Act & Assert
        mockMvc.perform(delete("/api/market-actors/{id}", testId))
                .andExpect(status().isNotFound());
    }
}