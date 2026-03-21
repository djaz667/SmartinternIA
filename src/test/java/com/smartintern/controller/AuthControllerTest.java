package com.smartintern.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartintern.dto.auth.RegisterRequest;
import com.smartintern.enums.Role;
import com.smartintern.exception.DuplicateResourceException;
import com.smartintern.exception.GlobalExceptionHandler;
import com.smartintern.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void register_validRequest_returns201() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .motDePasse("password123")
                .role(Role.ETUDIANT)
                .nom("Test")
                .prenom("User")
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(Map.of("message", "Compte créé avec succès"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Compte créé avec succès"));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("duplicate@example.com")
                .motDePasse("password123")
                .role(Role.ETUDIANT)
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException("Un compte avec cet email existe déjà"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Un compte avec cet email existe déjà"));
    }

    @Test
    void register_invalidEmail_returns400() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("not-an-email")
                .motDePasse("password123")
                .role(Role.ETUDIANT)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shortPassword_returns400() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .motDePasse("short")
                .role(Role.ETUDIANT)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_missingRole_returns400() throws Exception {
        String json = "{\"email\":\"test@example.com\",\"motDePasse\":\"password123\"}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_adminRole_returns400() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("admin@example.com")
                .motDePasse("password123")
                .role(Role.ADMIN)
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Seuls les rôles ETUDIANT et ENTREPRISE sont autorisés"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
