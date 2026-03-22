# Story 1.8: Publier une offre de stage (EN-01)

Status: review

## Story

As a entreprise (compte approuvé),
I want publier une offre de stage avec les détails requis,
so that les étudiants puissent découvrir et postuler à mes opportunités.

## Acceptance Criteria

1. **Endpoint POST /api/offres** : Accepte `OffreRequest`, accessible uniquement par ENTREPRISE avec statutCompte = APPROUVE. Retourne 201 Created avec `OffreResponse`.
2. **Champs obligatoires** : titre, domaine, description, durée, lieu, niveauRequis, competenceIds (liste d'IDs de compétences).
3. **Champ optionnel** : remuneration (décimal, nullable).
4. **Vérification approbation** : Si le compte entreprise n'est pas APPROUVE → 403 "Compte non encore approuvé".
5. **Association automatique** : L'offre est automatiquement liée à l'entreprise de l'utilisateur connecté (via le JWT userId → Entreprise).
6. **Compétences requises** : Les compétences sont liées via la table de jointure `offre_competence`. Les IDs doivent correspondre à des compétences existantes.
7. **Statut initial** : L'offre est créée avec `active = true` et `datePublication = now()`.
8. **Endpoint GET /api/offres/mes-offres** : L'entreprise connectée voit la liste de ses propres offres.
9. **Interface entreprise** : Formulaire de publication dans dashboard-entreprise.html avec champs dynamiques et multi-select pour les compétences.

## Tasks / Subtasks

- [x] Task 1 : Créer l'entité Offre et la table de jointure (AC: 2, 3, 6, 7)
  - [x] `model/Offre.java` : id, entreprise (ManyToOne), titre, domaine, description, duree, lieu, niveauRequis, remuneration (nullable), active (default true), datePublication
  - [x] Relation ManyToMany avec Competence via `offre_competence` (offre_id, competence_id)
  - [x] `model/Competence.java` : id, nom (unique), categorie (si pas déjà créé dans story 1-7)
- [x] Task 2 : Créer les repositories (AC: 5, 8)
  - [x] `OffreRepository.java` : findByEntrepriseId(Long entrepriseId), findByActiveTrue()
  - [x] `CompetenceRepository.java` : findByIdIn(List<Long> ids) (si pas déjà créé)
- [x] Task 3 : Créer les DTOs (AC: 1, 2, 3)
  - [x] `dto/offre/OffreRequest.java` : titre (@NotBlank), domaine (@NotBlank), description (@NotBlank), duree (@NotBlank), lieu (@NotBlank), niveauRequis (@NotBlank), competenceIds (@NotEmpty List<Long>), remuneration (nullable)
  - [x] `dto/offre/OffreResponse.java` : id, titre, domaine, description, duree, lieu, niveauRequis, remuneration, active, datePublication, entrepriseNom, competences (List<String>)
- [x] Task 4 : Implémenter OffreService.createOffre() (AC: 1, 4, 5, 6, 7)
  - [x] Récupérer l'entreprise associée au userId connecté
  - [x] Vérifier que le compte est APPROUVE
  - [x] Valider les competenceIds (doivent exister en BDD)
  - [x] Créer l'Offre avec entreprise, active=true, datePublication=now()
  - [x] Lier les compétences requises
  - [x] Sauvegarder et retourner OffreResponse
- [x] Task 5 : Implémenter OffreService.getMesOffres() (AC: 8)
  - [x] Récupérer les offres de l'entreprise connectée
  - [x] Retourner List<OffreResponse>
- [x] Task 6 : Créer OffreController (AC: 1, 8)
  - [x] POST `/api/offres` → createOffre() [@PreAuthorize("hasRole('ENTREPRISE')")]
  - [x] GET `/api/offres/mes-offres` → getMesOffres() [@PreAuthorize("hasRole('ENTREPRISE')")]
  - [x] GET `/api/offres` → getOffresActives() [accessible par ETUDIANT — pour Sprint 2 mais créer le endpoint maintenant]
  - [x] GET `/api/offres/{id}` → getOffreById() [authentifié]
- [x] Task 7 : Endpoint compétences admin (AC: 6)
  - [x] GET `/api/competences` → liste de toutes les compétences [authentifié]
  - [x] POST `/api/competences` → créer une compétence [ADMIN]
  - [x] `CompetenceController.java`
  - [x] `data.sql` : insérer des compétences de base (Java, Spring Boot, HTML, CSS, JavaScript, SQL, Python, React, Angular, etc.)
- [x] Task 8 : Interface entreprise — publier offre (AC: 9)
  - [x] Section "Publier une offre" dans dashboard-entreprise.html
  - [x] Formulaire avec tous les champs obligatoires
  - [x] Multi-select pour les compétences (charger depuis GET /api/competences)
  - [x] Section "Mes offres" affichant les offres publiées (tableau)
  - [x] Feedback succès/erreur après publication

## Dev Notes

### Dépendance
Dépend de 1-1 (Entreprise entity), 1-2 (JWT auth, api.js), 1-4 (dashboard-entreprise.html squelette), 1-6 (compte APPROUVE requis).

### Vérification approbation dans le Service
Le service doit vérifier que l'entreprise est bien approuvée avant de publier :
```java
User user = userRepository.findById(userId).orElseThrow();
if (user.getStatutCompte() != StatutCompte.APPROUVE) {
    throw new UnauthorizedException("Compte non encore approuvé");
}
```

### Table de jointure offre_competence
```sql
CREATE TABLE offre_competence (
    offre_id BIGINT NOT NULL,
    competence_id BIGINT NOT NULL,
    PRIMARY KEY (offre_id, competence_id),
    FOREIGN KEY (offre_id) REFERENCES offres(id),
    FOREIGN KEY (competence_id) REFERENCES competences(id)
);
```
En JPA : `@ManyToMany` avec `@JoinTable` dans Offre.java.

### Architecture
- `OffreController` + `OffreService` + `OffreRepository` — pattern standard
- Le endpoint GET /api/offres (liste publique) sera utilisé par les étudiants au Sprint 2 mais autant le créer maintenant
- `CompetenceController` est un petit controller utilitaire (CRUD simple)

### Données de démo
Insérer dans `data.sql` :
- ~15 compétences techniques courantes
- Optionnel : 1-2 offres de démo pour tester

### References
- [Source: architecture.md#API REST Offres] - POST /api/offres, GET /api/offres/mes-offres
- [Source: architecture.md#Modèle de Données] - Offre, Competence, offre_competence
- [Source: architecture.md#API REST Compétences] - GET/POST /api/competences
- [Source: prd.md#FR13] - Publier une offre avec champs obligatoires
- [Source: prd.md#FR16] - Publication qu'après validation du compte

## Change Log

- 2026-03-21: Implemented all 8 tasks — entities, repos, DTOs, service, controllers, data.sql seed, dashboard UI. 11 unit tests added (OffreServiceTest). Total: 84 tests passing, 0 failures.

## Dev Agent Record

### Agent Model Used
Claude Opus 4.6

### Debug Log References
N/A — no debug issues encountered

### Completion Notes List
- All 9 acceptance criteria met
- Offre entity with ManyToMany competences via offre_competence join table
- OffreService checks APPROUVE status before allowing publication (ForbiddenException if not)
- 4 REST endpoints: POST /api/offres, GET /api/offres, GET /api/offres/mes-offres, GET /api/offres/{id}
- CompetenceController: GET /api/competences + POST /api/competences (ADMIN only)
- 15 competences seeded in data.sql (Java, Spring Boot, Python, Node.js, SQL, MySQL, MongoDB, HTML, CSS, JavaScript, React, Angular, Docker, Git, Machine Learning)
- Dashboard-entreprise.html: publish form with competence chip multi-select + offres table with live data
- 11 unit tests in OffreServiceTest covering all service methods and error paths
- 84 total tests passing (73 existing + 11 new)

### File List
- `src/main/java/com/smartintern/model/Competence.java` (NEW)
- `src/main/java/com/smartintern/model/Offre.java` (NEW)
- `src/main/java/com/smartintern/repository/CompetenceRepository.java` (NEW)
- `src/main/java/com/smartintern/repository/OffreRepository.java` (NEW)
- `src/main/java/com/smartintern/dto/offre/OffreRequest.java` (NEW)
- `src/main/java/com/smartintern/dto/offre/OffreResponse.java` (NEW)
- `src/main/java/com/smartintern/service/OffreService.java` (NEW)
- `src/main/java/com/smartintern/controller/OffreController.java` (NEW)
- `src/main/java/com/smartintern/controller/CompetenceController.java` (NEW)
- `src/main/resources/data.sql` (MODIFIED — added 15 competences)
- `src/main/resources/static/pages/dashboard-entreprise.html` (MODIFIED — publish form + offres table)
- `src/test/java/com/smartintern/service/OffreServiceTest.java` (NEW — 11 tests)
