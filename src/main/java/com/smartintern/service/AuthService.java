package com.smartintern.service;

import com.smartintern.dto.auth.AuthResponse;
import com.smartintern.dto.auth.ConfirmResetRequest;
import com.smartintern.dto.auth.LoginRequest;
import com.smartintern.dto.auth.RegisterRequest;
import com.smartintern.dto.auth.ResetPasswordRequest;
import com.smartintern.enums.Role;
import com.smartintern.enums.StatutCompte;
import com.smartintern.exception.DuplicateResourceException;
import com.smartintern.exception.ForbiddenException;
import com.smartintern.exception.ResourceNotFoundException;
import com.smartintern.exception.UnauthorizedException;
import com.smartintern.model.Entreprise;
import com.smartintern.model.Etudiant;
import com.smartintern.model.Filiere;
import com.smartintern.model.User;
import com.smartintern.repository.EntrepriseRepository;
import com.smartintern.repository.EtudiantRepository;
import com.smartintern.repository.FiliereRepository;
import com.smartintern.repository.UserRepository;
import com.smartintern.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EtudiantRepository etudiantRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final FiliereRepository filiereRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private static final Set<Role> ROLES_INSCRIPTION_PUBLIQUE = Set.of(Role.ETUDIANT, Role.ENTREPRISE);

    @Transactional
    public Map<String, String> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Un compte avec cet email existe déjà");
        }

        if (!ROLES_INSCRIPTION_PUBLIQUE.contains(request.getRole())) {
            throw new IllegalArgumentException(
                    "Seuls les rôles ETUDIANT et ENTREPRISE sont autorisés à l'inscription");
        }

        User user = User.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(request.getRole())
                .statutCompte(StatutCompte.EN_ATTENTE)
                .build();
        userRepository.save(user);

        if (request.getRole() == Role.ETUDIANT) {
            createEtudiant(user, request);
        } else {
            createEntreprise(user, request);
        }

        return Map.of("message", "Compte créé avec succès. En attente d'approbation par l'administrateur.");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Identifiants invalides"));

        if (!passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            throw new UnauthorizedException("Identifiants invalides");
        }

        if (user.getStatutCompte() != StatutCompte.APPROUVE) {
            String message = switch (user.getStatutCompte()) {
                case EN_ATTENTE -> "Compte en attente d'approbation";
                case REFUSE -> "Compte refusé";
                case SUSPENDU -> "Compte suspendu";
                default -> "Compte non autorisé";
            };
            throw new ForbiddenException(message);
        }

        String token = jwtTokenProvider.generateToken(user);
        String nom = resolveUserName(user);

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .nom(nom)
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public Map<String, String> requestPasswordReset(ResetPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            log.info("Reset token pour {} : {}", user.getEmail(), token);

            return Map.of(
                    "message", "Si l'email existe, un lien de réinitialisation a été envoyé.",
                    "token", token
            );
        }

        return Map.of("message", "Si l'email existe, un lien de réinitialisation a été envoyé.");
    }

    @Transactional
    public Map<String, String> confirmPasswordReset(ConfirmResetRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token invalide ou expiré"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            throw new IllegalArgumentException("Token invalide ou expiré");
        }

        user.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return Map.of("message", "Mot de passe réinitialisé avec succès.");
    }

    private String resolveUserName(User user) {
        if (user.getRole() == Role.ETUDIANT) {
            return etudiantRepository.findByUserId(user.getId())
                    .map(Etudiant::getNom)
                    .orElse("");
        } else if (user.getRole() == Role.ENTREPRISE) {
            return entrepriseRepository.findByUserId(user.getId())
                    .map(Entreprise::getNom)
                    .orElse("");
        }
        return user.getEmail();
    }

    private void createEtudiant(User user, RegisterRequest request) {
        Etudiant.EtudiantBuilder builder = Etudiant.builder()
                .user(user)
                .nom(request.getNom())
                .prenom(request.getPrenom());

        if (request.getFiliereId() != null) {
            Filiere filiere = filiereRepository.findById(request.getFiliereId())
                    .orElseThrow(() -> new ResourceNotFoundException("Filière non trouvée"));
            builder.filiere(filiere);
        }

        etudiantRepository.save(builder.build());
    }

    private void createEntreprise(User user, RegisterRequest request) {
        Entreprise entreprise = Entreprise.builder()
                .user(user)
                .nom(request.getNomEntreprise())
                .secteur(request.getSecteur())
                .adresse(request.getAdresse())
                .telephone(request.getTelephone())
                .build();
        entrepriseRepository.save(entreprise);
    }
}
