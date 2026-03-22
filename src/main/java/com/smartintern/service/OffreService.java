package com.smartintern.service;

import com.smartintern.dto.offre.OffreRequest;
import com.smartintern.dto.offre.OffreResponse;
import com.smartintern.enums.StatutCompte;
import com.smartintern.exception.ForbiddenException;
import com.smartintern.exception.ResourceNotFoundException;
import com.smartintern.model.Competence;
import com.smartintern.model.Entreprise;
import com.smartintern.model.Offre;
import com.smartintern.model.User;
import com.smartintern.repository.CompetenceRepository;
import com.smartintern.repository.EntrepriseRepository;
import com.smartintern.repository.OffreRepository;
import com.smartintern.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OffreService {

    private final OffreRepository offreRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final UserRepository userRepository;
    private final CompetenceRepository competenceRepository;

    @Transactional
    public OffreResponse createOffre(Long userId, OffreRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve"));

        if (user.getStatutCompte() != StatutCompte.APPROUVE) {
            throw new ForbiddenException("Compte non encore approuve");
        }

        Entreprise entreprise = entrepriseRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil entreprise non trouve"));

        List<Competence> competences = competenceRepository.findByIdIn(request.getCompetenceIds());
        if (competences.size() != request.getCompetenceIds().size()) {
            throw new ResourceNotFoundException("Une ou plusieurs competences n'existent pas");
        }

        Offre offre = Offre.builder()
                .entreprise(entreprise)
                .titre(request.getTitre())
                .domaine(request.getDomaine())
                .description(request.getDescription())
                .duree(request.getDuree())
                .lieu(request.getLieu())
                .niveauRequis(request.getNiveauRequis())
                .remuneration(request.getRemuneration())
                .active(true)
                .competences(new HashSet<>(competences))
                .build();

        offreRepository.save(offre);
        return toResponse(offre);
    }

    public List<OffreResponse> getMesOffres(Long userId) {
        Entreprise entreprise = entrepriseRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil entreprise non trouve"));

        return offreRepository.findByEntrepriseId(entreprise.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OffreResponse> getOffresActives() {
        return offreRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public OffreResponse getOffreById(Long id) {
        Offre offre = offreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre non trouvee"));
        return toResponse(offre);
    }

    private OffreResponse toResponse(Offre offre) {
        return OffreResponse.builder()
                .id(offre.getId())
                .titre(offre.getTitre())
                .domaine(offre.getDomaine())
                .description(offre.getDescription())
                .duree(offre.getDuree())
                .lieu(offre.getLieu())
                .niveauRequis(offre.getNiveauRequis())
                .remuneration(offre.getRemuneration())
                .active(offre.isActive())
                .datePublication(offre.getDatePublication())
                .entrepriseNom(offre.getEntreprise().getNom())
                .entrepriseId(offre.getEntreprise().getId())
                .competences(offre.getCompetences().stream()
                        .map(Competence::getNom)
                        .toList())
                .build();
    }
}
