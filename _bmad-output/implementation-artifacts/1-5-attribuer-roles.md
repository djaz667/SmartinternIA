# Story 1.5: Attribuer des rôles aux utilisateurs (AD-01)

Status: ready-for-dev

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

- [ ] Task 1 : Créer les entités encadrants (AC: 3)
  - [ ] `model/EncadrantAcademique.java` : id, user (OneToOne), nom, prenom, departement, specialite
  - [ ] `model/EncadrantEntreprise.java` : id, user (OneToOne), entreprise (ManyToOne), nom, prenom, poste
  - [ ] `EncadrantAcademiqueRepository.java` : findByUserId(Long userId)
  - [ ] `EncadrantEntrepriseRepository.java` : findByUserId(Long userId), findByEntrepriseId(Long entrepriseId)
- [ ] Task 2 : Implémenter UserService.assignRole() (AC: 1, 2, 3, 4)
  - [ ] Charger le user par id (sinon 404)
  - [ ] Vérifier que le nouveau rôle != ADMIN (sinon 403)
  - [ ] Mettre à jour le rôle du user
  - [ ] Créer le profil associé si nécessaire (Etudiant, Entreprise, EncadrantAcademique, EncadrantEntreprise)
  - [ ] Sauvegarder et retourner le profil mis à jour
- [ ] Task 3 : Implémenter UserService.createEncadrantAcademique() (AC: 6)
  - [ ] Créer un User avec role=ENCADRANT_ACADEMIQUE, statutCompte=APPROUVE
  - [ ] Créer le EncadrantAcademique associé
  - [ ] Encoder mot de passe BCrypt
- [ ] Task 4 : Créer UserController (AC: 1, 6)
  - [ ] PUT `/api/users/{id}/role` → UserService.assignRole() [@PreAuthorize("hasRole('ADMIN')")]
  - [ ] POST `/api/users/encadrant-academique` → UserService.createEncadrantAcademique() [@PreAuthorize("hasRole('ADMIN')")]
  - [ ] GET `/api/users` → UserService.getAllUsers() [@PreAuthorize("hasRole('ADMIN')")]
- [ ] Task 5 : Créer les DTOs (AC: 1, 6)
  - [ ] `dto/user/RoleAssignRequest.java` : role (@NotNull)
  - [ ] `dto/user/UserResponse.java` : id, email, role, statutCompte, dateCreation, nom (résolu selon le rôle)
  - [ ] `dto/user/CreateEncadrantRequest.java` : email, motDePasse, nom, prenom, departement, specialite
- [ ] Task 6 : Mettre à jour SecurityConfig (AC: 1)
  - [ ] `/api/users/**` accessible par ADMIN pour les endpoints de gestion
  - [ ] Vérifier que les patterns de sécurité sont cohérents
- [ ] Task 7 : Interface admin — liste des utilisateurs (AC: 5)
  - [ ] Dans `dashboard-admin.html` : section "Gestion des utilisateurs"
  - [ ] Charger la liste des users via GET /api/users
  - [ ] Afficher tableau : email, rôle, statut, date création
  - [ ] Dropdown pour changer le rôle (appel PUT /api/users/{id}/role)
  - [ ] Bouton "Créer encadrant académique" ouvrant un formulaire modal

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
### Debug Log References
### Completion Notes List
### File List
