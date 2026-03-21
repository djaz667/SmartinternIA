package com.smartintern.service;

import com.smartintern.dto.user.CreateEncadrantRequest;
import com.smartintern.dto.user.UserResponse;
import com.smartintern.enums.Role;
import com.smartintern.enums.StatutCompte;
import com.smartintern.exception.DuplicateResourceException;
import com.smartintern.exception.ForbiddenException;
import com.smartintern.exception.ResourceNotFoundException;
import com.smartintern.model.EncadrantAcademique;
import com.smartintern.model.User;
import com.smartintern.repository.*;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EtudiantRepository etudiantRepository;
    @Mock
    private EntrepriseRepository entrepriseRepository;
    @Mock
    private EncadrantAcademiqueRepository encadrantAcademiqueRepository;
    @Mock
    private EncadrantEntrepriseRepository encadrantEntrepriseRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("user@test.com")
                .motDePasse("encoded")
                .role(Role.ETUDIANT)
                .statutCompte(StatutCompte.APPROUVE)
                .dateCreation(LocalDateTime.now())
                .build();
    }

    // === assignRole tests ===

    @Test
    void assignRole_validRole_updatesAndReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(encadrantAcademiqueRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(encadrantAcademiqueRepository.save(any(EncadrantAcademique.class)))
                .thenReturn(EncadrantAcademique.builder().build());

        UserResponse response = userService.assignRole(1L, Role.ENCADRANT_ACADEMIQUE);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getRole()).isEqualTo("ENCADRANT_ACADEMIQUE");
        verify(userRepository).save(testUser);
        verify(encadrantAcademiqueRepository).save(any(EncadrantAcademique.class));
    }

    @Test
    void assignRole_adminRole_throws403() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userService.assignRole(1L, Role.ADMIN))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Le rôle ADMIN ne peut pas être attribué via cet endpoint");
    }

    @Test
    void assignRole_userNotFound_throws404() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.assignRole(99L, Role.ETUDIANT))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Utilisateur non trouvé");
    }

    @Test
    void assignRole_profileAlreadyExists_doesNotDuplicate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(etudiantRepository.findByUserId(1L)).thenReturn(Optional.of(
                com.smartintern.model.Etudiant.builder().user(testUser).build()));

        userService.assignRole(1L, Role.ETUDIANT);

        verify(etudiantRepository, never()).save(any());
    }

    // === createEncadrantAcademique tests ===

    @Test
    void createEncadrantAcademique_success() {
        CreateEncadrantRequest request = CreateEncadrantRequest.builder()
                .email("encadrant@univ.tn")
                .motDePasse("password123")
                .nom("Ben Ali")
                .prenom("Mohamed")
                .departement("Informatique")
                .specialite("IA")
                .build();

        when(userRepository.existsByEmail("encadrant@univ.tn")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            u.setDateCreation(LocalDateTime.now());
            return u;
        });
        when(encadrantAcademiqueRepository.save(any(EncadrantAcademique.class)))
                .thenReturn(EncadrantAcademique.builder().build());

        UserResponse response = userService.createEncadrantAcademique(request);

        assertThat(response.getEmail()).isEqualTo("encadrant@univ.tn");
        assertThat(response.getRole()).isEqualTo("ENCADRANT_ACADEMIQUE");
        assertThat(response.getNom()).isEqualTo("Ben Ali");

        verify(userRepository).save(argThat(u ->
                u.getRole() == Role.ENCADRANT_ACADEMIQUE &&
                u.getStatutCompte() == StatutCompte.APPROUVE &&
                u.getMotDePasse().equals("encodedPwd")
        ));
        verify(encadrantAcademiqueRepository).save(argThat(e ->
                e.getNom().equals("Ben Ali") &&
                e.getPrenom().equals("Mohamed") &&
                e.getDepartement().equals("Informatique")
        ));
    }

    @Test
    void createEncadrantAcademique_duplicateEmail_throws409() {
        CreateEncadrantRequest request = CreateEncadrantRequest.builder()
                .email("existing@test.com")
                .motDePasse("password123")
                .nom("Test")
                .prenom("User")
                .build();

        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createEncadrantAcademique(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Un compte avec cet email existe déjà");
    }

    // === updateStatut tests ===

    @Test
    void updateStatut_approveUser_updatesStatut() {
        testUser.setStatutCompte(StatutCompte.EN_ATTENTE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(etudiantRepository.findByUserId(1L)).thenReturn(Optional.empty());

        UserResponse response = userService.updateStatut(1L, StatutCompte.APPROUVE);

        assertThat(response.getStatutCompte()).isEqualTo("APPROUVE");
        verify(userRepository).save(argThat(u -> u.getStatutCompte() == StatutCompte.APPROUVE));
    }

    @Test
    void updateStatut_rejectUser_updatesStatut() {
        testUser.setStatutCompte(StatutCompte.EN_ATTENTE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(etudiantRepository.findByUserId(1L)).thenReturn(Optional.empty());

        UserResponse response = userService.updateStatut(1L, StatutCompte.REFUSE);

        assertThat(response.getStatutCompte()).isEqualTo("REFUSE");
        verify(userRepository).save(argThat(u -> u.getStatutCompte() == StatutCompte.REFUSE));
    }

    @Test
    void updateStatut_userNotFound_throws404() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateStatut(99L, StatutCompte.APPROUVE))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Utilisateur non trouvé");
    }

    // === getAllUsers tests ===

    @Test
    void getAllUsers_returnsList() {
        User user2 = User.builder()
                .id(2L)
                .email("admin@test.com")
                .role(Role.ADMIN)
                .statutCompte(StatutCompte.APPROUVE)
                .dateCreation(LocalDateTime.now())
                .build();

        when(userRepository.findAll()).thenReturn(List.of(testUser, user2));
        when(etudiantRepository.findByUserId(1L)).thenReturn(Optional.empty());

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("user@test.com");
        assertThat(result.get(1).getEmail()).isEqualTo("admin@test.com");
    }
}
