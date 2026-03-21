package com.smartintern.service;

import com.smartintern.dto.auth.RegisterRequest;
import com.smartintern.enums.Role;
import com.smartintern.enums.StatutCompte;
import com.smartintern.exception.DuplicateResourceException;
import com.smartintern.model.Entreprise;
import com.smartintern.model.Etudiant;
import com.smartintern.model.Filiere;
import com.smartintern.model.User;
import com.smartintern.repository.EntrepriseRepository;
import com.smartintern.repository.EtudiantRepository;
import com.smartintern.repository.FiliereRepository;
import com.smartintern.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EtudiantRepository etudiantRepository;
    @Mock
    private EntrepriseRepository entrepriseRepository;
    @Mock
    private FiliereRepository filiereRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest etudiantRequest;
    private RegisterRequest entrepriseRequest;

    @BeforeEach
    void setUp() {
        etudiantRequest = RegisterRequest.builder()
                .email("etudiant@test.com")
                .motDePasse("password123")
                .role(Role.ETUDIANT)
                .nom("Dupont")
                .prenom("Jean")
                .filiereId(1L)
                .build();

        entrepriseRequest = RegisterRequest.builder()
                .email("entreprise@test.com")
                .motDePasse("password123")
                .role(Role.ENTREPRISE)
                .nomEntreprise("TechCorp")
                .secteur("Technologies")
                .adresse("Tunis")
                .telephone("+216 71 000 000")
                .build();
    }

    @Test
    void register_etudiant_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(filiereRepository.findById(1L)).thenReturn(Optional.of(
                Filiere.builder().id(1L).nom("Informatique").build()));
        when(etudiantRepository.save(any(Etudiant.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, String> result = authService.register(etudiantRequest);

        assertThat(result).containsKey("message");
        assertThat(result.get("message")).contains("succès");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("etudiant@test.com");
        assertThat(savedUser.getMotDePasse()).isEqualTo("encodedPassword");
        assertThat(savedUser.getRole()).isEqualTo(Role.ETUDIANT);
        assertThat(savedUser.getStatutCompte()).isEqualTo(StatutCompte.EN_ATTENTE);

        verify(etudiantRepository).save(any(Etudiant.class));
        verify(entrepriseRepository, never()).save(any(Entreprise.class));
    }

    @Test
    void register_entreprise_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(entrepriseRepository.save(any(Entreprise.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, String> result = authService.register(entrepriseRequest);

        assertThat(result).containsKey("message");

        ArgumentCaptor<Entreprise> captor = ArgumentCaptor.forClass(Entreprise.class);
        verify(entrepriseRepository).save(captor.capture());
        Entreprise savedEntreprise = captor.getValue();
        assertThat(savedEntreprise.getNom()).isEqualTo("TechCorp");
        assertThat(savedEntreprise.getSecteur()).isEqualTo("Technologies");

        verify(etudiantRepository, never()).save(any(Etudiant.class));
    }

    @Test
    void register_duplicateEmail_throws409() {
        when(userRepository.existsByEmail("etudiant@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(etudiantRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email existe déjà");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_adminRole_throwsIllegalArgument() {
        RegisterRequest adminRequest = RegisterRequest.builder()
                .email("admin@test.com")
                .motDePasse("password123")
                .role(Role.ADMIN)
                .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.register(adminRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ETUDIANT et ENTREPRISE");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_encadrantAcademiqueRole_throwsIllegalArgument() {
        RegisterRequest request = RegisterRequest.builder()
                .email("enc@test.com")
                .motDePasse("password123")
                .role(Role.ENCADRANT_ACADEMIQUE)
                .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void register_encadrantEntrepriseRole_throwsIllegalArgument() {
        RegisterRequest request = RegisterRequest.builder()
                .email("enc@test.com")
                .motDePasse("password123")
                .role(Role.ENCADRANT_ENTREPRISE)
                .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void register_passwordIsEncoded() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedValue");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(filiereRepository.findById(1L)).thenReturn(Optional.of(
                Filiere.builder().id(1L).nom("Informatique").build()));
        when(etudiantRepository.save(any(Etudiant.class))).thenAnswer(i -> i.getArgument(0));

        authService.register(etudiantRequest);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getMotDePasse()).isEqualTo("$2a$10$hashedValue");
        assertThat(captor.getValue().getMotDePasse()).isNotEqualTo("password123");
    }

    @Test
    void register_initialStatus_isEnAttente() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(entrepriseRepository.save(any(Entreprise.class))).thenAnswer(i -> i.getArgument(0));

        authService.register(entrepriseRequest);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getStatutCompte()).isEqualTo(StatutCompte.EN_ATTENTE);
    }
}
