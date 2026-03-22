package com.smartintern.controller;

import com.smartintern.dto.etudiant.EtudiantResponse;
import com.smartintern.dto.etudiant.UpdateEtudiantRequest;
import com.smartintern.exception.ResourceNotFoundException;
import com.smartintern.model.Etudiant;
import com.smartintern.repository.EtudiantRepository;
import com.smartintern.repository.UserRepository;
import com.smartintern.service.EtudiantService;
import com.smartintern.service.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/etudiants")
@RequiredArgsConstructor
public class EtudiantController {

    private final EtudiantService etudiantService;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final EtudiantRepository etudiantRepository;

    @PostMapping("/me/cv")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<Map<String, String>> uploadCv(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        Map<String, String> response = etudiantService.uploadCv(userId, file);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me/cv")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<Map<String, String>> deleteCv(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        Map<String, String> response = etudiantService.deleteCv(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<EtudiantResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        EtudiantResponse response = etudiantService.getMyProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<EtudiantResponse> updateMyProfile(
            @Valid @RequestBody UpdateEtudiantRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        EtudiantResponse response = etudiantService.updateMyProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/cv")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTREPRISE', 'ENCADRANT_ACADEMIQUE', 'ENCADRANT_ENTREPRISE')")
    public ResponseEntity<Resource> downloadCv(@PathVariable Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        if (etudiant.getCvPath() == null) {
            throw new ResourceNotFoundException("Aucun CV disponible pour cet étudiant");
        }

        Resource resource = fileStorageService.loadFile(etudiant.getCvPath());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"cv_" + id + ".pdf\"")
                .body(resource);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCADRANT_ACADEMIQUE', 'ENCADRANT_ENTREPRISE')")
    public ResponseEntity<EtudiantResponse> getProfile(@PathVariable Long id) {
        EtudiantResponse response = etudiantService.getProfile(id);
        return ResponseEntity.ok(response);
    }

    private Long resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"))
                .getId();
    }
}
