# Story 1.6: Gérer les comptes utilisateurs (AD-02)

Status: review

## Story

As a administrateur,
I want approuver, refuser ou suspendre les comptes utilisateurs,
so that seuls les utilisateurs légitimes accèdent à la plateforme.

## Acceptance Criteria

1. **Endpoint PUT /api/users/{id}/statut** : Accepte `{statutCompte}` (APPROUVE, REFUSE, SUSPENDU), accessible uniquement par ADMIN. Retourne le profil mis à jour.
2. **Comptes en attente** : L'admin voit la liste des comptes avec `statutCompte = EN_ATTENTE` dans une section dédiée du dashboard admin.
3. **Approbation** : Passer un compte de EN_ATTENTE à APPROUVE permet à l'utilisateur de se connecter.
4. **Refus** : Passer un compte à REFUSE empêche la connexion avec message "Compte refusé".
5. **Suspension** : Passer un compte APPROUVE à SUSPENDU bloque la connexion avec message "Compte suspendu".
6. **Filtres** : L'admin peut filtrer les utilisateurs par statut (EN_ATTENTE, APPROUVE, REFUSE, SUSPENDU) et par rôle.
7. **Compteur** : Le dashboard admin affiche le nombre de comptes en attente d'approbation (badge visuel).

## Tasks / Subtasks

- [x] Task 1 : Implémenter UserService.updateStatut() (AC: 1, 3, 4, 5)
  - [x] Charger user par id (sinon 404)
  - [x] Valider la transition de statut (pas de transition vers EN_ATTENTE)
  - [x] Mettre à jour statutCompte
  - [x] Sauvegarder et retourner UserResponse
- [x] Task 2 : Implémenter UserService.getUsersByFilters() (AC: 2, 6)
  - [x] GET /api/users?statut=EN_ATTENTE&role=ETUDIANT
  - [x] Filtrage par statut et/ou rôle (paramètres optionnels)
  - [x] Retourner List<UserResponse>
- [x] Task 3 : Implémenter UserService.countPendingUsers() (AC: 7)
  - [x] Compter les users avec statutCompte = EN_ATTENTE
  - [x] GET /api/users/pending/count → retourne {count: N}
- [x] Task 4 : Ajouter les endpoints dans UserController (AC: 1, 6, 7)
  - [x] PUT `/api/users/{id}/statut` → updateStatut() [@PreAuthorize("hasRole('ADMIN')")]
  - [x] GET `/api/users` avec @RequestParam statut, role → getUsersByFilters()
  - [x] GET `/api/users/pending/count` → countPendingUsers()
- [x] Task 5 : Ajouter à UserRepository (AC: 2, 6, 7)
  - [x] `findByStatutCompte(StatutCompte statut)` : List<User>
  - [x] `findByRoleAndStatutCompte(Role role, StatutCompte statut)` : List<User>
  - [x] `countByStatutCompte(StatutCompte statut)` : Long
- [x] Task 6 : Interface admin — gestion des comptes (AC: 2, 6, 7)
  - [x] Section "Comptes en attente" dans dashboard-admin.html avec badge compteur
  - [x] Tableau des comptes en attente avec boutons "Approuver" / "Refuser"
  - [x] Filtres dropdown : par statut, par rôle
  - [x] Bouton "Suspendre" sur les comptes approuvés
  - [x] Feedback visuel après chaque action (succès/erreur)

## Dev Notes

### Dépendance
Dépend de 1-1 (User entity, StatutCompte enum), 1-2 (JWT auth), 1-5 (UserController, UserResponse DTO).

### Lien avec le login (Story 1-2)
La vérification du statut du compte est déjà implémentée dans `AuthService.login()` (story 1-2). Cette story complète le circuit en permettant à l'admin de changer les statuts.

### Transitions de statut autorisées
```
EN_ATTENTE → APPROUVE (approbation)
EN_ATTENTE → REFUSE (refus)
APPROUVE → SUSPENDU (suspension)
SUSPENDU → APPROUVE (réactivation)
```
Interdites : retour vers EN_ATTENTE, passage direct REFUSE → APPROUVE (doit recréer un compte).

### Architecture
- Réutiliser `UserController` créé dans story 1-5
- `UserResponse` DTO déjà créé dans story 1-5 — le réutiliser
- Les filtres sont des @RequestParam optionnels sur GET /api/users

### References
- [Source: architecture.md#API REST Utilisateurs] - PUT /api/users/{id}/statut
- [Source: prd.md#FR5] - Approuver ou refuser les comptes
- [Source: prd.md#NFR4] - Isolation des données

## Dev Agent Record

### Agent Model Used
Claude Opus 4.6

### Debug Log References
- Aucun problème rencontré

### Completion Notes List
- Task 1: updateStatut() enrichi avec validateStatutTransition() — interdit transition vers EN_ATTENTE, interdit REFUSE→APPROUVE. Transitions autorisées : EN_ATTENTE→APPROUVE, EN_ATTENTE→REFUSE, APPROUVE→SUSPENDU, SUSPENDU→APPROUVE.
- Task 2: getUsers(statut, role) remplace getAllUsers() — filtrage conditionnel : les deux params, un seul, ou aucun. Utilise findByRoleAndStatutCompte, findByStatutCompte, findByRole, ou findAll selon les cas.
- Task 3: countPendingUsers() utilise countByStatutCompte(EN_ATTENTE). Endpoint GET /api/users/pending/count retourne {count: N}.
- Task 4: UserController mis à jour — GET /api/users accepte @RequestParam statut et role optionnels, GET /api/users/pending/count ajouté avec @PreAuthorize ADMIN.
- Task 5: UserRepository enrichi avec findByStatutCompte, findByRole, findByRoleAndStatutCompte, countByStatutCompte — méthodes JPA dérivées.
- Task 6: dashboard-admin.html enrichi — badge compteur rouge en-attente, filtres dropdown statut/rôle, bouton "Suspendre" sur comptes APPROUVE (non-admin), bouton "Réactiver" sur comptes SUSPENDU, rafraîchissement compteur après chaque action.

### File List
- src/main/java/com/smartintern/repository/UserRepository.java (MODIFIED)
- src/main/java/com/smartintern/service/UserService.java (MODIFIED)
- src/main/java/com/smartintern/controller/UserController.java (MODIFIED)
- src/main/resources/static/pages/dashboard-admin.html (MODIFIED)
- src/test/java/com/smartintern/service/UserServiceTest.java (MODIFIED)

## Change Log
- 2026-03-21: Implémentation complète de la story 1-6 Gérer les comptes utilisateurs — validation transitions statut (EN_ATTENTE→APPROUVE/REFUSE, APPROUVE↔SUSPENDU), filtres par statut/rôle sur GET /api/users, compteur EN_ATTENTE avec badge, boutons Suspendre/Réactiver, interface admin enrichie. 56 tests passent (0 régression).
