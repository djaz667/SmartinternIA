# Story 1.8: Publier une offre de stage (EN-01)

Status: ready-for-dev

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

- [ ] Task 1 : Créer l'entité Offre et la table de jointure (AC: 2, 3, 6, 7)
  - [ ] `model/Offre.java` : id, entreprise (ManyToOne), titre, domaine, description, duree, lieu, niveauRequis, remuneration (nullable), active (default true), datePublication
  - [ ] Relation ManyToMany avec Competence via `offre_competence` (offre_id, competence_id)
  - [ ] `model/Competence.java` : id, nom (unique), categorie (si pas déjà créé dans story 1-7)
- [ ] Task 2 : Créer les repositories (AC: 5, 8)
  - [ ] `OffreRepository.java` : findByEntrepriseId(Long entrepriseId), findByActiveTrue()
  - [ ] `CompetenceRepository.java` : findByIdIn(List<Long> ids) (si pas déjà créé)
- [ ] Task 3 : Créer les DTOs (AC: 1, 2, 3)
  - [ ] `dto/offre/OffreRequest.java` : titre (@NotBlank), domaine (@NotBlank), description (@NotBlank), duree (@NotBlank), lieu (@NotBlank), niveauRequis (@NotBlank), competenceIds (@NotEmpty List<Long>), remuneration (nullable)
  - [ ] `dto/offre/OffreResponse.java` : id, titre, domaine, description, duree, lieu, niveauRequis, remuneration, active, datePublication, entrepriseNom, competences (List<String>)
- [ ] Task 4 : Implémenter OffreService.createOffre() (AC: 1, 4, 5, 6, 7)
  - [ ] Récupérer l'entreprise associée au userId connecté
  - [ ] Vérifier que le compte est APPROUVE
  - [ ] Valider les competenceIds (doivent exister en BDD)
  - [ ] Créer l'Offre avec entreprise, active=true, datePublication=now()
  - [ ] Lier les compétences requises
  - [ ] Sauvegarder et retourner OffreResponse
- [ ] Task 5 : Implémenter OffreService.getMesOffres() (AC: 8)
  - [ ] Récupérer les offres de l'entreprise connectée
  - [ ] Retourner List<OffreResponse>
- [ ] Task 6 : Créer OffreController (AC: 1, 8)
  - [ ] POST `/api/offres` → createOffre() [@PreAuthorize("hasRole('ENTREPRISE')")]
  - [ ] GET `/api/offres/mes-offres` → getMesOffres() [@PreAuthorize("hasRole('ENTREPRISE')")]
  - [ ] GET `/api/offres` → getOffresActives() [accessible par ETUDIANT — pour Sprint 2 mais créer le endpoint maintenant]
  - [ ] GET `/api/offres/{id}` → getOffreById() [authentifié]
- [ ] Task 7 : Endpoint compétences admin (AC: 6)
  - [ ] GET `/api/competences` → liste de toutes les compétences [authentifié]
  - [ ] POST `/api/competences` → créer une compétence [ADMIN]
  - [ ] `CompetenceController.java`
  - [ ] `data.sql` : insérer des compétences de base (Java, Spring Boot, HTML, CSS, JavaScript, SQL, Python, React, Angular, etc.)
- [ ] Task 8 : Interface entreprise — publier offre (AC: 9)
  - [ ] Section "Publier une offre" dans dashboard-entreprise.html
  - [ ] Formulaire avec tous les champs obligatoires
  - [ ] Multi-select pour les compétences (charger depuis GET /api/competences)
  - [ ] Section "Mes offres" affichant les offres publiées (tableau)
  - [ ] Feedback succès/erreur après publication

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

## Dev Agent Record

### Agent Model Used
### Debug Log References
### Completion Notes List
### File List
