package com.smartintern.service;

import com.smartintern.dto.auth.AuthResponse;
import com.smartintern.dto.auth.LoginRequest;
import com.smartintern.enums.Role;
import com.smartintern.enums.StatutCompte;
import com.smartintern.exception.ForbiddenException;
import com.smartintern.exception.UnauthorizedException;
import com.smartintern.model.Etudiant;
import com.smartintern.model.User;
import com.smartintern.repository.EntrepriseRepository;
import com.smartintern.repository.EtudiantRepository;
import com.smartintern.repository.FiliereRepository;
import com.smartintern.repository.UserRepository;
import com.smartintern.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceLoginTest {

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
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User approvedEtudiant;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        approvedEtudiant = User.builder()
                .id(1L)
                .email("etudiant@test.com")
                .motDePasse("encodedPassword")
                .role(Role.ETUDIANT)
                .statutCompte(StatutCompte.APPROUVE)
                .build();

        loginRequest = LoginRequest.builder()
                .email("etudiant@test.com")
                .motDePasse("password123")
                .build();
    }

    @Test
    void login_approvedUser_returnsAuthResponse() {
        when(userRepository.findByEmail("etudiant@test.com")).thenReturn(Optional.of(approvedEtudiant));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(approvedEtudiant)).thenReturn("jwt.token.here");
        when(etudiantRepository.findByUserId(1L)).thenReturn(Optional.of(
                Etudiant.builder().nom("Dupont").build()));

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("jwt.token.here");
        assertThat(response.getRole()).isEqualTo("ETUDIANT");
        assertThat(response.getNom()).isEqualTo("Dupont");
        assertThat(response.getEmail()).isEqualTo("etudiant@test.com");
    }

    @Test
    void login_unknownEmail_throws401() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        LoginRequest request = LoginRequest.builder()
                .email("unknown@test.com")
                .motDePasse("password123")
                .build();

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Identifiants invalides");
    }

    @Test
    void login_wrongPassword_throws401() {
        when(userRepository.findByEmail("etudiant@test.com")).thenReturn(Optional.of(approvedEtudiant));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        LoginRequest request = LoginRequest.builder()
                .email("etudiant@test.com")
                .motDePasse("wrongPassword")
                .build();

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Identifiants invalides");
    }

    @Test
    void login_enAttenteStatus_throws403() {
        User pendingUser = User.builder()
                .id(2L)
                .email("pending@test.com")
                .motDePasse("encodedPassword")
                .role(Role.ETUDIANT)
                .statutCompte(StatutCompte.EN_ATTENTE)
                .build();

        when(userRepository.findByEmail("pending@test.com")).thenReturn(Optional.of(pendingUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        LoginRequest request = LoginRequest.builder()
                .email("pending@test.com")
                .motDePasse("password123")
                .build();

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Compte en attente d'approbation");
    }

    @Test
    void login_refuseStatus_throws403() {
        User refusedUser = User.builder()
                .id(3L)
                .email("refused@test.com")
                .motDePasse("encodedPassword")
                .role(Role.ETUDIANT)
                .statutCompte(StatutCompte.REFUSE)
                .build();

        when(userRepository.findByEmail("refused@test.com")).thenReturn(Optional.of(refusedUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        LoginRequest request = LoginRequest.builder()
                .email("refused@test.com")
                .motDePasse("password123")
                .build();

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Compte refusé");
    }

    @Test
    void login_suspenduStatus_throws403() {
        User suspendedUser = User.builder()
                .id(4L)
                .email("suspended@test.com")
                .motDePasse("encodedPassword")
                .role(Role.ETUDIANT)
                .statutCompte(StatutCompte.SUSPENDU)
                .build();

        when(userRepository.findByEmail("suspended@test.com")).thenReturn(Optional.of(suspendedUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        LoginRequest request = LoginRequest.builder()
                .email("suspended@test.com")
                .motDePasse("password123")
                .build();

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Compte suspendu");
    }
}
