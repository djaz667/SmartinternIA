package com.smartintern.controller;

import com.smartintern.dto.offre.OffreRequest;
import com.smartintern.dto.offre.OffreResponse;
import com.smartintern.exception.ResourceNotFoundException;
import com.smartintern.repository.UserRepository;
import com.smartintern.service.OffreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offres")
@RequiredArgsConstructor
public class OffreController {

    private final OffreService offreService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('ENTREPRISE')")
    public ResponseEntity<OffreResponse> createOffre(
            @Valid @RequestBody OffreRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        OffreResponse response = offreService.createOffre(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mes-offres")
    @PreAuthorize("hasRole('ENTREPRISE')")
    public ResponseEntity<List<OffreResponse>> getMesOffres(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        List<OffreResponse> response = offreService.getMesOffres(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OffreResponse>> getOffresActives() {
        List<OffreResponse> response = offreService.getOffresActives();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OffreResponse> getOffreById(@PathVariable Long id) {
        OffreResponse response = offreService.getOffreById(id);
        return ResponseEntity.ok(response);
    }

    private Long resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve"))
                .getId();
    }
}
