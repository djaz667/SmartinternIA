package com.smartintern.service;

import com.smartintern.dto.etudiant.EtudiantResponse;
import com.smartintern.dto.etudiant.UpdateEtudiantRequest;
import com.smartintern.exception.ResourceNotFoundException;
import com.smartintern.model.Etudiant;
import com.smartintern.model.Filiere;
import com.smartintern.model.User;
import com.smartintern.repository.EtudiantRepository;
import com.smartintern.repository.FiliereRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;
    @Mock
    private FiliereRepository filiereRepository;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private EtudiantService etudiantService;

    private Etudiant testEtudiant;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("etudiant@test.com").build();
        testEtudiant = Etudiant.builder()
                .id(10L)
                .user(testUser)
                .nom("Dupont")
                .prenom("Jean")
                .bio("Étudiant en informatique")
                .build();
    }

    // === uploadCv tests ===

    @Test
    void uploadCv_validPdf_savesAndReturnsCvPath() {
        MultipartFile file = new MockMultipartFile("file", "cv.pdf", "application/pdf", new byte[1024]);
        when(etudiantRepository.findByUserId(1L)).thenReturn(Optional.of(testEtudiant));
        when(fileStorageService.storeFile(any(), eq("cv"), eq("cv_1"))).thenReturn("cv/cv_1_123.pdf");
        when(etudiantRepository.save(any())).thenReturn(testEtudiant);

        Map<String, String> result = etudiantService.uploadCv(1L, file);

        assertThat(result.get("cvPath")).isEqualTo("cv/cv_1_123.pdf");
        verify(fileStorageService).storeFile(any(), eq("cv"), eq("cv_1"));
        verify(etudiantRepository).save(testEtudiant);
    }

    @Test
    void uploadCv_invalidContentType_throws400() {
        MultipartFile file = new MockMultipartFile("file", "cv.docx", "application/msword", new byte[100]);

        assertThatThrownBy(() -> etudiantService.uploadCv(1L, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Format non autorisé, seul le PDF est accepté");
    }

    @Test
    void uploadCv_tooLarge_throws400() {
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6 Mo
        MultipartFile file = new MockMultipartFile("file", "cv.pdf", "application/pdf", largeContent);

        assertThatThrownBy(() -> etudiantService.uploadCv(1L, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Fichier trop volumineux (max 5 Mo)");
    }

    @Test
    void uploadCv_replacesExistingCv() {
        testEtudiant.setCvPath("cv/old_cv.pdf");
        MultipartFile file = new MockMultipartFile("file", "cv.pdf", "application/pdf", new byte[100]);
        when(etudiantRepository.findByUserId(1L)).thenReturn(Optional.of(testEtudiant));
        when(fileStorageService.storeFile(any(), eq("cv"), eq("cv_1"))).thenReturn("cv/cv_1_new.pdf");
        when(etudiantRepository.save(any())).thenReturn(testEtudiant);

        etudiantService.uploadCv(1L, file);

        verify(fileStorageService).deleteFile("cv/old_cv.pdf");
        verify(fileStorageService).storeFile(any(), eq("cv"), eq("cv_1"));
    }

    @Test
    void uploadCv_profileNotFound_throws404() {
        MultipartFile file = new MockMultipartFile("file", "cv.pdf", "application/pdf", new byte[100]);
        when(etudiantRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> etudiantService.uploadCv(99L, file))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Profil étudiant non trouvé");
    }

    // === deleteCv tests ===

    @Test
    void deleteCv_validDelete_removesCvAndReturnsMessage() {
        testEtudiant.setCvPath("cv/cv_1_123.pdf");
        when(etudiantRepository.findByUserId(1L)).thenReturn(Optional.of(testEtudiant));
        when(etudiantRepository.save(any())).thenReturn(testEtudiant);

        Map<String, String> result = etudiantService.deleteCv(1L);

        assertThat(result.get("message")).isEqualTo("CV supprimé avec succès");
        assertThat(testEtudiant.getCvPath()).isNull();
        verify(fileStorageService).deleteFile("cv/cv_1_123.pdf");
        verify(etudiantRepository).save(testEtudiant);
    }

    @Test
    void deleteCv_noCvToDelete_throws404() {
        testEtudiant.setCvPath(null);
        when(etudiantRepository.findByUserId(1L)).thenReturn(Optional.of(testEtudiant));

        assertThatThrownBy(() -> etudiantService.deleteCv(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Aucun CV à supprimer");
    }

    @Test
    void deleteCv_profileNotFound_throws404() {
        when(etudiantRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> etudiantService.deleteCv(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Profil étudiant non trouvé");
    }

    // === getMyProfile tests ===

    @Test
    void getMyProfile_returnsProfile() {
        when(etudiantRepository.findByUserId(1L)).thenReturn(Optional.of(testEtudiant));

        EtudiantResponse response = etudiantService.getMyProfile(1L);

        assertThat(response.getNom()).isEqualTo("Dupont");
        assertThat(response.getPrenom()).isEqualTo("Jean");
        assertThat(response.getEmail()).isEqualTo("etudiant@test.com");
    }

    @Test
    void getMyProfile_notFound_throws404() {
        when(etudiantRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> etudiantService.getMyProfile(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // === updateMyProfile tests ===

    @Test
    void updateMyProfile_updatesFields() {
        when(etudiantRepository.findByUserId(1L)).thenReturn(Optional.of(testEtudiant));
        when(etudiantRepository.save(any())).thenReturn(testEtudiant);

        UpdateEtudiantRequest request = UpdateEtudiantRequest.builder()
                .nom("NouveauNom")
                .prenom("NouveauPrenom")
                .bio("Nouvelle bio")
                .build();

        etudiantService.updateMyProfile(1L, request);

        assertThat(testEtudiant.getNom()).isEqualTo("NouveauNom");
        assertThat(testEtudiant.getPrenom()).isEqualTo("NouveauPrenom");
        assertThat(testEtudiant.getBio()).isEqualTo("Nouvelle bio");
    }

    @Test
    void updateMyProfile_withFiliere_updatesFiliere() {
        Filiere filiere = Filiere.builder().id(5L).nom("Informatique").build();
        when(etudiantRepository.findByUserId(1L)).thenReturn(Optional.of(testEtudiant));
        when(filiereRepository.findById(5L)).thenReturn(Optional.of(filiere));
        when(etudiantRepository.save(any())).thenReturn(testEtudiant);

        UpdateEtudiantRequest request = UpdateEtudiantRequest.builder()
                .filiereId(5L)
                .build();

        etudiantService.updateMyProfile(1L, request);

        assertThat(testEtudiant.getFiliere()).isEqualTo(filiere);
    }
}
