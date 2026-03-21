package com.smartintern.service;

import com.smartintern.dto.auth.ConfirmResetRequest;
import com.smartintern.dto.auth.ResetPasswordRequest;
import com.smartintern.enums.Role;
import com.smartintern.enums.StatutCompte;
import com.smartintern.model.User;
import com.smartintern.repository.EntrepriseRepository;
import com.smartintern.repository.EtudiantRepository;
import com.smartintern.repository.FiliereRepository;
import com.smartintern.repository.UserRepository;
import com.smartintern.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceResetPasswordTest {

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

    @Test
    void requestPasswordReset_existingEmail_generatesToken() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.ETUDIANT)
                .statutCompte(StatutCompte.APPROUVE)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        ResetPasswordRequest request = ResetPasswordRequest.builder().email("test@example.com").build();
        Map<String, String> result = authService.requestPasswordReset(request);

        assertThat(result).containsKey("message");
        assertThat(result).containsKey("token");
        assertThat(result.get("token")).isNotBlank();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getResetToken()).isNotNull();
        assertThat(captor.getValue().getResetTokenExpiry()).isAfter(LocalDateTime.now().plusMinutes(59));
    }

    @Test
    void requestPasswordReset_unknownEmail_returnsGenericMessage() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        ResetPasswordRequest request = ResetPasswordRequest.builder().email("unknown@example.com").build();
        Map<String, String> result = authService.requestPasswordReset(request);

        assertThat(result).containsKey("message");
        assertThat(result).doesNotContainKey("token");
        assertThat(result.get("message")).contains("Si l'email existe");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void confirmPasswordReset_validToken_updatesPassword() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .resetToken("valid-token-uuid")
                .resetTokenExpiry(LocalDateTime.now().plusMinutes(30))
                .build();

        when(userRepository.findByResetToken("valid-token-uuid")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        ConfirmResetRequest request = ConfirmResetRequest.builder()
                .token("valid-token-uuid")
                .nouveauMotDePasse("newPassword123")
                .build();

        Map<String, String> result = authService.confirmPasswordReset(request);

        assertThat(result.get("message")).contains("succès");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getMotDePasse()).isEqualTo("encodedNewPassword");
        assertThat(saved.getResetToken()).isNull();
        assertThat(saved.getResetTokenExpiry()).isNull();
    }

    @Test
    void confirmPasswordReset_invalidToken_throws400() {
        when(userRepository.findByResetToken("bad-token")).thenReturn(Optional.empty());

        ConfirmResetRequest request = ConfirmResetRequest.builder()
                .token("bad-token")
                .nouveauMotDePasse("newPassword123")
                .build();

        assertThatThrownBy(() -> authService.confirmPasswordReset(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token invalide ou expiré");
    }

    @Test
    void confirmPasswordReset_expiredToken_throws400() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .resetToken("expired-token")
                .resetTokenExpiry(LocalDateTime.now().minusMinutes(10))
                .build();

        when(userRepository.findByResetToken("expired-token")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        ConfirmResetRequest request = ConfirmResetRequest.builder()
                .token("expired-token")
                .nouveauMotDePasse("newPassword123")
                .build();

        assertThatThrownBy(() -> authService.confirmPasswordReset(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token invalide ou expiré");

        // Token should be invalidated
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getResetToken()).isNull();
    }
}
