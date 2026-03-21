package com.smartintern.service;

import com.smartintern.dto.auth.RegisterRequest;
import com.smartintern.enums.Role;
import com.smartintern.enums.StatutCompte;
import com.smartintern.exception.DuplicateResourceException;
import com.smartintern.exception.ResourceNotFoundException;
import com.smartintern.model.Entreprise;
import com.smartintern.model.Etudiant;
import com.smartintern.model.Filiere;
import com.smartintern.model.User;
import com.smartintern.repository.EntrepriseRepository;
import com.smartintern.repository.EtudiantRepository;
import com.smartintern.repository.FiliereRepository;
import com.smartintern.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EtudiantRepository etudiantRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final FiliereRepository filiereRepository;
    private final PasswordEncoder passwordEncoder;

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
