package com.smartintern.controller;

import com.smartintern.dto.auth.AuthResponse;
import com.smartintern.dto.auth.ConfirmResetRequest;
import com.smartintern.dto.auth.LoginRequest;
import com.smartintern.dto.auth.RegisterRequest;
import com.smartintern.dto.auth.ResetPasswordRequest;
import com.smartintern.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, String> response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        Map<String, String> response = authService.requestPasswordReset(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<Map<String, String>> confirmResetPassword(@Valid @RequestBody ConfirmResetRequest request) {
        Map<String, String> response = authService.confirmPasswordReset(request);
        return ResponseEntity.ok(response);
    }
}
