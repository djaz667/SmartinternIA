package com.smartintern.service;

import com.smartintern.dto.etudiant.EtudiantResponse;
import com.smartintern.dto.etudiant.UpdateEtudiantRequest;
import com.smartintern.exception.ResourceNotFoundException;
import com.smartintern.model.Etudiant;
import com.smartintern.model.Filiere;
import com.smartintern.repository.EtudiantRepository;
import com.smartintern.repository.FiliereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final FiliereRepository filiereRepository;
    private final FileStorageService fileStorageService;

    private static final long MAX_CV_SIZE = 5 * 1024 * 1024; // 5 Mo
    private static final String CV_CONTENT_TYPE = "application/pdf";

    @Transactional
    public Map<String, String> uploadCv(Long userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        if (!CV_CONTENT_TYPE.equals(file.getContentType())) {
            throw new IllegalArgumentException("Format non autorisé, seul le PDF est accepté");
        }

        if (file.getSize() > MAX_CV_SIZE) {
            throw new IllegalArgumentException("Fichier trop volumineux (max 5 Mo)");
        }

        Etudiant etudiant = etudiantRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil étudiant non trouvé"));

        // Supprimer l'ancien CV si existant
        if (etudiant.getCvPath() != null) {
            fileStorageService.deleteFile(etudiant.getCvPath());
        }

        String cvPath = fileStorageService.storeFile(file, "cv", "cv_" + userId);
        etudiant.setCvPath(cvPath);
        etudiantRepository.save(etudiant);

        return Map.of("message", "CV téléversé avec succès", "cvPath", cvPath);
    }

    @Transactional
    public Map<String, String> deleteCv(Long userId) {
        Etudiant etudiant = etudiantRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil étudiant non trouvé"));

        if (etudiant.getCvPath() == null) {
            throw new ResourceNotFoundException("Aucun CV à supprimer");
        }

        fileStorageService.deleteFile(etudiant.getCvPath());
        etudiant.setCvPath(null);
        etudiantRepository.save(etudiant);

        return Map.of("message", "CV supprimé avec succès");
    }

    public EtudiantResponse getMyProfile(Long userId) {
        Etudiant etudiant = etudiantRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil étudiant non trouvé"));
        return toResponse(etudiant);
    }

    public EtudiantResponse getProfile(Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));
        return toResponse(etudiant);
    }

    @Transactional
    public EtudiantResponse updateMyProfile(Long userId, UpdateEtudiantRequest request) {
        Etudiant etudiant = etudiantRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil étudiant non trouvé"));

        if (request.getNom() != null) etudiant.setNom(request.getNom());
        if (request.getPrenom() != null) etudiant.setPrenom(request.getPrenom());
        if (request.getNiveauAcademique() != null) etudiant.setNiveauAcademique(request.getNiveauAcademique());
        if (request.getBio() != null) etudiant.setBio(request.getBio());

        if (request.getFiliereId() != null) {
            Filiere filiere = filiereRepository.findById(request.getFiliereId())
                    .orElseThrow(() -> new ResourceNotFoundException("Filière non trouvée"));
            etudiant.setFiliere(filiere);
        }

        etudiantRepository.save(etudiant);
        return toResponse(etudiant);
    }

    private EtudiantResponse toResponse(Etudiant etudiant) {
        return EtudiantResponse.builder()
                .id(etudiant.getId())
                .nom(etudiant.getNom())
                .prenom(etudiant.getPrenom())
                .filiere(etudiant.getFiliere() != null ? etudiant.getFiliere().getNom() : null)
                .filiereId(etudiant.getFiliere() != null ? etudiant.getFiliere().getId() : null)
                .niveauAcademique(etudiant.getNiveauAcademique())
                .cvPath(etudiant.getCvPath())
                .bio(etudiant.getBio())
                .email(etudiant.getUser() != null ? etudiant.getUser().getEmail() : null)
                .build();
    }
}
