# Story 1.5: Attribuer des rôles aux utilisateurs (AD-01)

Status: review

## Story

As a administrateur,
I want attribuer des rôles aux utilisateurs,
so that chaque acteur ait les permissions adaptées à sa fonction.

## Acceptance Criteria

1. **Endpoint PUT /api/users/{id}/role** : Accepte `{role}` (enum Role), accessible uniquement par ADMIN. Retourne le profil utilisateur mis à jour.
2. **Rôles assignables** : ETUDIANT, ENTREPRISE, ENCADRANT_ACADEMIQUE, ENCADRANT_ENTREPRISE. Le rôle ADMIN ne peut PAS être attribué via cet endpoint (sécurité).
3. **Création de profil associé** : Si le rôle change, le profil associé est créé automatiquement. Ex: si on passe un user de ETUDIANT à ENCADRANT_ACADEMIQUE, un EncadrantAcademique est créé.
4. **Validation** : L'utilisateur cible doit exister (sinon 404). Le rôle doit être valide (sinon 400).
5. **Interface admin** : Section dans `dashboard-admin.html` listant les utilisateurs avec un dropdown pour modifier le rôle.
6. **Création de comptes encadrants** : L'admin peut créer des comptes ENCADRANT_ACADEMIQUE directement depuis l'interface (FR7). Formulaire : email, mot de passe, nom, prénom, département, spécialité.

## Tasks / Subtasks

- [x] Task 1 : Créer les entités encadrants (AC: 3)
  - [x] `model/EncadrantAcademique.java` : id, user (OneToOne), nom, prenom, departement, specialite
  - [x] `model/EncadrantEntreprise.java` : id, user (OneToOne), entreprise (ManyToOne), nom, prenom, poste
  - [x] `EncadrantAcademiqueRepository.java` : findByUserId(Long userId)
  - [x] `EncadrantEntrepriseRepository.java` : findByUserId(Long userId), findByEntrepriseId(Long entrepriseId)
- [x] Task 2 : Implémenter UserService.assignRole() (AC: 1, 2, 3, 4)
  - [x] Charger le user par id (sinon 404)
  - [x] Vérifier que le nouveau rôle != ADMIN (sinon 403)
  - [x] Mettre à jour le rôle du user
  - [x] Créer le profil associé si nécessaire (Etudiant, Entreprise, EncadrantAcademique, EncadrantEntreprise)
  - [x] Sauvegarder et retourner le profil mis à jour
- [x] Task 3 : Implémenter UserService.createEncadrantAcademique() (AC: 6)
  - [x] Créer un User avec role=ENCADRANT_ACADEMIQUE, statutCompte=APPROUVE
  - [x] Créer le EncadrantAcademique associé
  - [x] Encoder mot de passe BCrypt
- [x] Task 4 : Créer UserController (AC: 1, 6)
  - [x] PUT `/api/users/{id}/role` → UserService.assignRole() [@PreAuthorize("hasRole('ADMIN')")]
  - [x] POST `/api/users/encadrant-academique` → UserService.createEncadrantAcademique() [@PreAuthorize("hasRole('ADMIN')")]
  - [x] GET `/api/users` → UserService.getAllUsers() [@PreAuthorize("hasRole('ADMIN')")]
- [x] Task 5 : Créer les DTOs (AC: 1, 6)
  - [x] `dto/user/RoleAssignRequest.java` : role (@NotNull)
  - [x] `dto/user/UserResponse.java` : id, email, role, statutCompte, dateCreation, nom (résolu selon le rôle)
  - [x] `dto/user/CreateEncadrantRequest.java` : email, motDePasse, nom, prenom, departement, specialite
- [x] Task 6 : Mettre à jour SecurityConfig (AC: 1)
  - [x] `/api/users/**` accessible par ADMIN pour les endpoints de gestion
  - [x] Vérifier que les patterns de sécurité sont cohérents
- [x] Task 7 : Interface admin — liste des utilisateurs (AC: 5)
  - [x] Dans `dashboard-admin.html` : section "Gestion des utilisateurs"
  - [x] Charger la liste des users via GET /api/users
  - [x] Afficher tableau : email, rôle, statut, date création
  - [x] Dropdown pour changer le rôle (appel PUT /api/users/{id}/role)
  - [x] Bouton "Créer encadrant académique" ouvrant un formulaire modal

## Dev Notes

### Dépendance
Dépend de 1-1 et 1-2 (User entity, SecurityConfig, JWT auth, pages de base).

### 5 rôles du système
```java
public enum Role {
    ADMIN,
    ETUDIANT,
    ENTREPRISE,
    ENCADRANT_ACADEMIQUE,
    ENCADRANT_ENTREPRISE
}
```
- ADMIN : créé uniquement via data.sql (compte initial)
- ETUDIANT, ENTREPRISE : s'inscrivent via register
- ENCADRANT_ACADEMIQUE : créé par l'admin (FR7)
- ENCADRANT_ENTREPRISE : créé par l'entreprise (FR8, story future)

### Sécurité
- L'endpoint PUT /role ne doit JAMAIS permettre d'attribuer le rôle ADMIN
- Seul un ADMIN peut accéder aux endpoints `/api/users/**` de gestion
- `@PreAuthorize("hasRole('ADMIN')")` sur chaque méthode du controller

### Architecture
- `UserController` est distinct de `AuthController` — Auth gère l'inscription/connexion, User gère la gestion des comptes
- Le DTO `UserResponse` résout le nom depuis la table profil associée (Etudiant.nom, Entreprise.nom, etc.)

### References
- [Source: architecture.md#API REST Utilisateurs] - PUT /api/users/{id}/role, GET /api/users
- [Source: prd.md#FR6] - Attribuer des rôles
- [Source: prd.md#FR7] - Créer des comptes encadrants académiques
- [Source: prd.md#NFR3] - Vérification du rôle sur chaque endpoint
- [Source: prd.md#NFR4] - Isolation des données par rôle

## Dev Agent Record

### Agent Model Used
Claude Opus 4.6

### Debug Log References
- Aucun problème rencontré

### Completion Notes List
- Task 1: Entités EncadrantAcademique (encadrants_academiques) et EncadrantEntreprise (encadrants_entreprises) créées avec @OneToOne User, champs métier (departement, specialite pour académique ; entreprise ManyToOne, poste pour entreprise). Repositories avec findByUserId et findByEntrepriseId.
- Task 2: UserService.assignRole() — charge user par id (404), bloque rôle ADMIN (403), met à jour le rôle, crée le profil associé automatiquement via createProfileIfNeeded() (switch sur les 4 rôles assignables, vérifie qu'un profil n'existe pas déjà avant création).
- Task 3: UserService.createEncadrantAcademique() — crée User avec role=ENCADRANT_ACADEMIQUE, statutCompte=APPROUVE, mot de passe BCrypt encodé. Crée le EncadrantAcademique associé avec nom, prénom, département, spécialité.
- Task 4: UserController créé avec 3 endpoints : PUT /{id}/role, POST /encadrant-academique, GET /. Tous protégés par @PreAuthorize("hasRole('ADMIN')").
- Task 5: 3 DTOs créés — RoleAssignRequest (@NotNull role), UserResponse (id, email, role, statutCompte, dateCreation, nom résolu par rôle), CreateEncadrantRequest (@Email, @NotBlank, @Size).
- Task 6: SecurityConfig mis à jour avec @EnableMethodSecurity pour activer @PreAuthorize. Handler AccessDeniedException ajouté dans GlobalExceptionHandler (403).
- Task 7: dashboard-admin.html enrichi — tableau des utilisateurs chargé via GET /api/users, dropdown rôle par utilisateur (sauf ADMIN), badges statut colorés, bouton "Créer encadrant académique" avec formulaire modal complet.

### File List
- src/main/java/com/smartintern/model/EncadrantAcademique.java (NEW)
- src/main/java/com/smartintern/model/EncadrantEntreprise.java (NEW)
- src/main/java/com/smartintern/repository/EncadrantAcademiqueRepository.java (NEW)
- src/main/java/com/smartintern/repository/EncadrantEntrepriseRepository.java (NEW)
- src/main/java/com/smartintern/dto/user/RoleAssignRequest.java (NEW)
- src/main/java/com/smartintern/dto/user/UserResponse.java (NEW)
- src/main/java/com/smartintern/dto/user/CreateEncadrantRequest.java (NEW)
- src/main/java/com/smartintern/service/UserService.java (NEW)
- src/main/java/com/smartintern/controller/UserController.java (NEW)
- src/main/java/com/smartintern/config/SecurityConfig.java (MODIFIED)
- src/main/java/com/smartintern/exception/GlobalExceptionHandler.java (MODIFIED)
- src/main/resources/static/pages/dashboard-admin.html (MODIFIED)
- src/main/resources/static/js/auth.js (MODIFIED)
- src/test/java/com/smartintern/service/UserServiceTest.java (NEW)

## Change Log
- 2026-03-21: Implémentation complète de la story 1-5 Attribuer des rôles — entités EncadrantAcademique/EncadrantEntreprise, UserService (assignRole, createEncadrantAcademique, getAllUsers), UserController avec @PreAuthorize ADMIN, DTOs, @EnableMethodSecurity, interface admin avec tableau utilisateurs et formulaire modal de création encadrant. 45 tests passent (0 régression).
