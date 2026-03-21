package com.smartintern.service;

import com.smartintern.dto.user.CreateEncadrantRequest;
import com.smartintern.dto.user.UserResponse;
import com.smartintern.enums.Role;
import com.smartintern.enums.StatutCompte;
import com.smartintern.exception.DuplicateResourceException;
import com.smartintern.exception.ForbiddenException;
import com.smartintern.exception.ResourceNotFoundException;
import com.smartintern.model.*;
import com.smartintern.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EtudiantRepository etudiantRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final EncadrantAcademiqueRepository encadrantAcademiqueRepository;
    private final EncadrantEntrepriseRepository encadrantEntrepriseRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse assignRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (newRole == Role.ADMIN) {
            throw new ForbiddenException("Le rôle ADMIN ne peut pas être attribué via cet endpoint");
        }

        user.setRole(newRole);
        userRepository.save(user);

        createProfileIfNeeded(user, newRole);

        return toUserResponse(user);
    }

    @Transactional
    public UserResponse createEncadrantAcademique(CreateEncadrantRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Un compte avec cet email existe déjà");
        }

        User user = User.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(Role.ENCADRANT_ACADEMIQUE)
                .statutCompte(StatutCompte.APPROUVE)
                .build();
        userRepository.save(user);

        EncadrantAcademique encadrant = EncadrantAcademique.builder()
                .user(user)
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .departement(request.getDepartement())
                .specialite(request.getSpecialite())
                .build();
        encadrantAcademiqueRepository.save(encadrant);

        return toUserResponse(user, request.getNom());
    }

    @Transactional
    public UserResponse updateStatut(Long userId, StatutCompte newStatut) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        validateStatutTransition(user.getStatutCompte(), newStatut);

        user.setStatutCompte(newStatut);
        userRepository.save(user);

        return toUserResponse(user);
    }

    private void validateStatutTransition(StatutCompte current, StatutCompte target) {
        if (target == StatutCompte.EN_ATTENTE) {
            throw new IllegalArgumentException("Transition vers EN_ATTENTE non autorisée");
        }
        if (current == StatutCompte.REFUSE && target == StatutCompte.APPROUVE) {
            throw new IllegalArgumentException("Un compte refusé ne peut pas être approuvé directement");
        }
    }

    public List<UserResponse> getUsers(StatutCompte statut, Role role) {
        List<User> users;
        if (statut != null && role != null) {
            users = userRepository.findByRoleAndStatutCompte(role, statut);
        } else if (statut != null) {
            users = userRepository.findByStatutCompte(statut);
        } else if (role != null) {
            users = userRepository.findByRole(role);
        } else {
            users = userRepository.findAll();
        }
        return users.stream().map(this::toUserResponse).collect(Collectors.toList());
    }

    public long countPendingUsers() {
        return userRepository.countByStatutCompte(StatutCompte.EN_ATTENTE);
    }

    private void createProfileIfNeeded(User user, Role role) {
        switch (role) {
            case ETUDIANT -> {
                if (etudiantRepository.findByUserId(user.getId()).isEmpty()) {
                    etudiantRepository.save(Etudiant.builder().user(user).build());
                }
            }
            case ENTREPRISE -> {
                if (entrepriseRepository.findByUserId(user.getId()).isEmpty()) {
                    entrepriseRepository.save(Entreprise.builder().user(user).build());
                }
            }
            case ENCADRANT_ACADEMIQUE -> {
                if (encadrantAcademiqueRepository.findByUserId(user.getId()).isEmpty()) {
                    encadrantAcademiqueRepository.save(EncadrantAcademique.builder().user(user).build());
                }
            }
            case ENCADRANT_ENTREPRISE -> {
                if (encadrantEntrepriseRepository.findByUserId(user.getId()).isEmpty()) {
                    encadrantEntrepriseRepository.save(EncadrantEntreprise.builder().user(user).build());
                }
            }
            default -> { }
        }
    }

    private UserResponse toUserResponse(User user) {
        String nom = resolveUserName(user);
        return toUserResponse(user, nom);
    }

    private UserResponse toUserResponse(User user, String nom) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .statutCompte(user.getStatutCompte().name())
                .dateCreation(user.getDateCreation() != null ? user.getDateCreation().toString() : null)
                .nom(nom)
                .build();
    }

    private String resolveUserName(User user) {
        return switch (user.getRole()) {
            case ETUDIANT -> etudiantRepository.findByUserId(user.getId())
                    .map(Etudiant::getNom).orElse("");
            case ENTREPRISE -> entrepriseRepository.findByUserId(user.getId())
                    .map(Entreprise::getNom).orElse("");
            case ENCADRANT_ACADEMIQUE -> encadrantAcademiqueRepository.findByUserId(user.getId())
                    .map(EncadrantAcademique::getNom).orElse("");
            case ENCADRANT_ENTREPRISE -> encadrantEntrepriseRepository.findByUserId(user.getId())
                    .map(EncadrantEntreprise::getNom).orElse("");
            default -> user.getEmail();
        };
    }
}
