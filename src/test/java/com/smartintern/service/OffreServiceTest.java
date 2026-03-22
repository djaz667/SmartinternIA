package com.smartintern.service;

import com.smartintern.dto.offre.OffreRequest;
import com.smartintern.dto.offre.OffreResponse;
import com.smartintern.enums.Role;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OffreServiceTest {

    @Mock
    private OffreRepository offreRepository;
    @Mock
    private EntrepriseRepository entrepriseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CompetenceRepository competenceRepository;

    @InjectMocks
    private OffreService offreService;

    private User testUser;
    private Entreprise testEntreprise;
    private Competence compJava;
    private Competence compSpring;
    private OffreRequest validRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("entreprise@test.com")
                .role(Role.ENTREPRISE)
                .statutCompte(StatutCompte.APPROUVE)
                .build();

        testEntreprise = Entreprise.builder()
                .id(10L)
                .user(testUser)
                .nom("TechCorp")
                .secteur("IT")
                .build();

        compJava = Competence.builder().id(1L).nom("Java").categorie("Backend").build();
        compSpring = Competence.builder().id(2L).nom("Spring Boot").categorie("Backend").build();

        validRequest = OffreRequest.builder()
                .titre("Developpeur Java")
                .domaine("Developpement")
                .description("Stage en developpement Java/Spring")
                .duree("3 mois")
                .lieu("Tunis")
                .niveauRequis("3eme annee")
                .competenceIds(List.of(1L, 2L))
                .remuneration(new BigDecimal("500"))
                .build();
    }

    // === createOffre tests ===

    @Test
    void createOffre_valid_returnsOffreResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(entrepriseRepository.findByUserId(1L)).thenReturn(Optional.of(testEntreprise));
        when(competenceRepository.findByIdIn(List.of(1L, 2L))).thenReturn(List.of(compJava, compSpring));
        when(offreRepository.save(any())).thenAnswer(invocation -> {
            Offre offre = invocation.getArgument(0);
            offre.setId(100L);
            offre.setDatePublication(LocalDateTime.now());
            return offre;
        });

        OffreResponse response = offreService.createOffre(1L, validRequest);

        assertThat(response.getTitre()).isEqualTo("Developpeur Java");
        assertThat(response.getEntrepriseNom()).isEqualTo("TechCorp");
        assertThat(response.isActive()).isTrue();
        assertThat(response.getCompetences()).containsExactlyInAnyOrder("Java", "Spring Boot");
        assertThat(response.getRemuneration()).isEqualByComparingTo(new BigDecimal("500"));
        verify(offreRepository).save(any());
    }

    @Test
    void createOffre_compteNonApprouve_throws403() {
        testUser.setStatutCompte(StatutCompte.EN_ATTENTE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> offreService.createOffre(1L, validRequest))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Compte non encore approuve");
    }

    @Test
    void createOffre_entrepriseNonTrouvee_throws404() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(entrepriseRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offreService.createOffre(1L, validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Profil entreprise non trouve");
    }

    @Test
    void createOffre_competenceInvalide_throws404() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(entrepriseRepository.findByUserId(1L)).thenReturn(Optional.of(testEntreprise));
        when(competenceRepository.findByIdIn(List.of(1L, 2L))).thenReturn(List.of(compJava));

        assertThatThrownBy(() -> offreService.createOffre(1L, validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Une ou plusieurs competences n'existent pas");
    }

    @Test
    void createOffre_userNonTrouve_throws404() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offreService.createOffre(99L, validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Utilisateur non trouve");
    }

    @Test
    void createOffre_sansRemuneration_ok() {
        validRequest.setRemuneration(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(entrepriseRepository.findByUserId(1L)).thenReturn(Optional.of(testEntreprise));
        when(competenceRepository.findByIdIn(List.of(1L, 2L))).thenReturn(List.of(compJava, compSpring));
        when(offreRepository.save(any())).thenAnswer(invocation -> {
            Offre offre = invocation.getArgument(0);
            offre.setId(101L);
            offre.setDatePublication(LocalDateTime.now());
            return offre;
        });

        OffreResponse response = offreService.createOffre(1L, validRequest);

        assertThat(response.getRemuneration()).isNull();
    }

    // === getMesOffres tests ===

    @Test
    void getMesOffres_returnsEntrepriseOffres() {
        Offre offre = Offre.builder()
                .id(1L).titre("Stage Java").entreprise(testEntreprise)
                .domaine("IT").description("desc").duree("3m").lieu("Tunis")
                .niveauRequis("L3").active(true).datePublication(LocalDateTime.now())
                .competences(new HashSet<>(Set.of(compJava)))
                .build();
        when(entrepriseRepository.findByUserId(1L)).thenReturn(Optional.of(testEntreprise));
        when(offreRepository.findByEntrepriseId(10L)).thenReturn(List.of(offre));

        List<OffreResponse> result = offreService.getMesOffres(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitre()).isEqualTo("Stage Java");
    }

    @Test
    void getMesOffres_entrepriseNonTrouvee_throws404() {
        when(entrepriseRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offreService.getMesOffres(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // === getOffresActives tests ===

    @Test
    void getOffresActives_returnsActiveOffres() {
        Offre offre = Offre.builder()
                .id(1L).titre("Stage Python").entreprise(testEntreprise)
                .domaine("Data").description("desc").duree("6m").lieu("Sfax")
                .niveauRequis("M1").active(true).datePublication(LocalDateTime.now())
                .competences(new HashSet<>())
                .build();
        when(offreRepository.findByActiveTrue()).thenReturn(List.of(offre));

        List<OffreResponse> result = offreService.getOffresActives();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
    }

    // === getOffreById tests ===

    @Test
    void getOffreById_found_returnsResponse() {
        Offre offre = Offre.builder()
                .id(5L).titre("Stage React").entreprise(testEntreprise)
                .domaine("Frontend").description("desc").duree("2m").lieu("Tunis")
                .niveauRequis("L3").active(true).datePublication(LocalDateTime.now())
                .competences(new HashSet<>())
                .build();
        when(offreRepository.findById(5L)).thenReturn(Optional.of(offre));

        OffreResponse result = offreService.getOffreById(5L);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getTitre()).isEqualTo("Stage React");
    }

    @Test
    void getOffreById_notFound_throws404() {
        when(offreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offreService.getOffreById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
